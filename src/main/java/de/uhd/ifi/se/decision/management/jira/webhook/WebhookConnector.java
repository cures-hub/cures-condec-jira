package de.uhd.ifi.se.decision.management.jira.webhook;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpsURL;
import org.apache.commons.httpclient.methods.PostMethod;
import org.jgrapht.Graphs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;

/**
 * Webhook class that posts changed decision knowledge to a given URL. The
 * format of the posted data is determined by the {@link WebhookType}.
 */
public class WebhookConnector {
	private static final Logger LOGGER = LoggerFactory.getLogger(WebhookConnector.class);
	private String url;
	private String secret;
	private String projectKey;
	private List<Long> elementIds;
	private Collection<String> rootTypes;
	private WebhookType type;

	public WebhookConnector(String projectKey, String webhookUrl, String webhookSecret, Collection<String> rootTypes) {
		this.projectKey = projectKey;
		this.url = webhookUrl;
		this.secret = webhookSecret;
		this.rootTypes = new ArrayList<>();
		if (rootTypes == null || rootTypes.isEmpty()) {
			this.rootTypes.add("Task");
		} else {
			this.rootTypes = rootTypes;
		}
		this.elementIds = new ArrayList<Long>();
		this.type = WebhookType.getTypeFromUrl(url);
	}

	public WebhookConnector(String projectKey) {
		this(projectKey, ConfigPersistenceManager.getWebhookConfiguration(projectKey).getWebhookUrl(),
				ConfigPersistenceManager.getWebhookConfiguration(projectKey).getWebhookSecret(),
				ConfigPersistenceManager.getWebhookConfiguration(projectKey).getObservedTypes());
	}

	public boolean sendElement(KnowledgeElement sendElement) {
		return sendElement(sendElement, "new");
	}

	public boolean sendElement(KnowledgeElement sendElement, String event) {
		if (sendElement == null || event == null) {
			return false;
		}
		boolean isSubmitted = false;

		if (!checkIfDataIsValid(sendElement)) {
			return isSubmitted;
		}

		switch (type) {
		case TREANT:
			isSubmitted = postKnowledgeTree(sendElement);
			break;
		case SLACK:
			isSubmitted = postKnowledgeElement(sendElement, event);
			break;
		default:
			LOGGER.error("Webhook type is unknown.");
			break;
		}
		return isSubmitted;
	}

	public boolean deleteElement(KnowledgeElement elementToBeDeleted, ApplicationUser user) {
		if (!checkIfDataIsValid(elementToBeDeleted)) {
			return false;
		}

		List<KnowledgeElement> rootElements = getWebhookRootElements(elementToBeDeleted);
		String type = elementToBeDeleted.getTypeAsString();

		for (String rootType : rootTypes) {
			if (rootType.equalsIgnoreCase(type)) {
				rootElements.remove(elementToBeDeleted);
			}
		}

		boolean isDeleted = KnowledgePersistenceManager.getOrCreate(projectKey)
				.deleteKnowledgeElement(elementToBeDeleted, user);
		if (isDeleted) {
			isDeleted = postKnowledgeTrees(rootElements);
		}
		return isDeleted;
	}

	public boolean sendTestPost() {
		PostMethod postMethod = new PostMethod();

		KnowledgeElement testElement = new KnowledgeElement(1, this.projectKey, "i");
		testElement.setType(KnowledgeType.ISSUE);
		testElement.setSummary("Test Summary");

		AbstractWebookContentProvider provider = null;
		if (type == WebhookType.TREANT) {
			provider = new WebhookContentProviderForTreant(projectKey, testElement, secret, type);
		}
		if (type == WebhookType.SLACK) {
			provider = new WebhookContentProviderForSlack(projectKey, testElement, type);

		}
		if (provider != null) {
			postMethod = provider.createTestPostMethod();
		}

		return executePostMethod(postMethod);
	}

	/**
	 * Is Used for Treant
	 */
	private boolean postKnowledgeTrees(List<KnowledgeElement> rootElements) {
		for (KnowledgeElement rootElement : rootElements) {
			if (!postKnowledgeTree(rootElement)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Is Used for Treant
	 */
	private List<KnowledgeElement> getWebhookRootElements(KnowledgeElement element) {
		List<KnowledgeElement> webhookRootElements = new ArrayList<KnowledgeElement>();
		KnowledgeGraph graph = KnowledgeGraph.getInstance(projectKey);
		List<KnowledgeElement> linkedElements = Graphs.neighborListOf(graph, element);
		linkedElements.add(element);
		for (KnowledgeElement linkedElement : linkedElements) {
			if (elementIds.contains(linkedElement.getId())) {
				continue;
			}
			elementIds.add(linkedElement.getId());
			String type = linkedElement.getTypeAsString();
			for (String rootType : rootTypes) {
				if (rootType.equalsIgnoreCase(type)) {
					webhookRootElements.add(linkedElement);
				}
			}
			webhookRootElements.addAll(getWebhookRootElements(linkedElement));
		}
		return webhookRootElements;
	}

	/**
	 * Is Used for Treant (singel element)
	 */
	private boolean postKnowledgeTree(KnowledgeElement rootElement) {
		WebhookContentProviderForTreant provider = new WebhookContentProviderForTreant(projectKey, rootElement, secret,
				type);
		PostMethod postMethod = provider.createPostMethod();
		return executePostMethod(postMethod);
	}

	private boolean executePostMethod(PostMethod postMethod) {
		try {
			HttpClient httpClient = new HttpClient();
			postMethod.setURI(new HttpsURL(url));
			int httpResponse = httpClient.executeMethod(postMethod);
			if (httpResponse >= 200 && httpResponse < 300) {
				return true;
			}
			LOGGER.error("Could not send webhook data. The HTTP response code is: " + httpResponse);
		} catch (IOException | IllegalArgumentException e) {
			LOGGER.error("Could not send webhook data because of " + e.getMessage());
		}
		return false;
	}

	/**
	 * Is Used for Slack
	 */
	private boolean postKnowledgeElement(KnowledgeElement changedElement, String event) {
		WebhookContentProviderForSlack provider = new WebhookContentProviderForSlack(projectKey, changedElement, type);
		PostMethod postMethod = provider.createPostMethod(event);
		return executePostMethod(postMethod);
	}

	private boolean checkIfDataIsValid(KnowledgeElement changedElement) {
		if (url == null || url.isBlank()) {
			LOGGER.error("Could not trigger webhook data because the url is missing.");
			return false;
		}
		if ((secret == null || secret.isBlank()) && type != WebhookType.SLACK) {
			LOGGER.error("Could not trigger webhook data because the secret is missing.");
			return false;
		}
		if (projectKey == null || projectKey.isBlank()) {
			LOGGER.error("Could not trigger webhook data because the project key is missing.");
			return false;
		}
		if (changedElement == null) {
			LOGGER.error("Could not trigger webhook data because the changed element is null.");
			return false;
		}
		return true;
	}

	/**
	 * @return url of the receiver as a string, e.g.
	 *         "https://hooks.slack.com/services/...".
	 */
	public String getUrl() {
		return this.url;
	}

	/**
	 * @param url
	 *            of the receiver, e.g. "https://hooks.slack.com/services/...". This
	 *            also determines the {@link WebhookType}.
	 */
	public void setUrl(String url) {
		this.url = url;
		this.type = WebhookType.getTypeFromUrl(url);
	}
}
