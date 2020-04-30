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
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;

/**
 * Webhook class that posts changed decision knowledge to a given URL.
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
		this(projectKey, ConfigPersistenceManager.getWebhookUrl(projectKey),
				ConfigPersistenceManager.getWebhookSecret(projectKey),
				ConfigPersistenceManager.getEnabledWebhookTypes(projectKey));
	}


	public boolean sendElement(KnowledgeElement sendElement){
		return sendElement(sendElement, "new");

	}


	public boolean sendElement(KnowledgeElement sendElement, String event){

		boolean isSubmitted = false;

		if (!checkIfDataIsValid(sendElement)) {
			return isSubmitted;
		}
		if (type == WebhookType.TREANT) {
			isSubmitted = postKnowledgeTree(sendElement);
			return isSubmitted;
		}
		if (type == WebhookType.SLACK) {
			isSubmitted = postKnowledgeElement(sendElement, event);
			return isSubmitted;
		}
		return false;

	}

/*
	public boolean sendTestPost(WebhookType type){
		KnowledgeElement testElement = new KnowledgeElement();
		testElement.setType(KnowledgeType.ISSUE);
		testElement.setDescription("Test descirption");
		testElement.setSummary("Test Summary");
		testElement.setKey("projectKey");

		boolean isSubmitted = false;

		if (!checkIfDataIsValid(testElement)) {
			return isSubmitted;
		}
		if (type == WebhookType.TREANT) {
			isSubmitted = postKnowledgeTree(testElement);
			return isSubmitted;
		}
		if (type == WebhookType.SLACK) {
			isSubmitted = postKnowledgeElement(testElement, "test");
			return isSubmitted;
		}
		return false;
	}



	public boolean sendNewElement(KnowledgeElement newElement) {
		boolean isSubmitted = false;
		if (!checkIfDataIsValid(newElement)) {
			return isSubmitted;
		}
		if (type == WebhookType.TREANT) {
			List<KnowledgeElement> rootElements = getWebhookRootElements(newElement);
			isSubmitted = postKnowledgeTrees(rootElements);
			return isSubmitted;
		}
		if (type == WebhookType.SLACK) {
			isSubmitted = postKnowledgeElement(newElement, "new");
			return isSubmitted;
		}
		return false;
	}

	public boolean sendElementChanges(KnowledgeElement changedElement) {
		boolean isSubmitted = false;
		if (!checkIfDataIsValid(changedElement)) {
			return isSubmitted;
		}
		if (type == WebhookType.TREANT) {
			List<KnowledgeElement> rootElements = getWebhookRootElements(changedElement);
			isSubmitted = postKnowledgeTrees(rootElements);
			return isSubmitted;
		}
		if (type == WebhookType.SLACK) {
			isSubmitted = postKnowledgeElement(changedElement, "changed");
			return isSubmitted;
		}
		return false;
	}
	*/

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
		KnowledgeGraph graph = KnowledgeGraph.getOrCreate(projectKey);
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
		WebhookContentProviderForTreant provider = new WebhookContentProviderForTreant(projectKey, rootElement, secret, type);
		PostMethod postMethod = provider.createPostMethodForTreant();

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
		PostMethod postMethod = provider.createPostMethodForSlack(changedElement, event);
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

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
		this.type = WebhookType.getTypeFromUrl(url);
	}
}
