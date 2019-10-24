package de.uhd.ifi.se.decision.management.jira.webhook;

import com.atlassian.jira.user.ApplicationUser;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.AbstractPersistenceManagerForSingleLocation;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpsURL;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
	}

	public WebhookConnector(String projectKey) {
		this(projectKey, ConfigPersistenceManager.getWebhookUrl(projectKey),
				ConfigPersistenceManager.getWebhookSecret(projectKey),
				ConfigPersistenceManager.getEnabledWebhookTypes(projectKey));
	}

	public boolean sendElementChanges(DecisionKnowledgeElement changedElement) {
		boolean isSubmitted = false;
		if (!checkIfDataIsValid(changedElement)) {
			return isSubmitted;
		}
		List<DecisionKnowledgeElement> rootElements = getWebhookRootElements(changedElement);
		isSubmitted = postKnowledgeTrees(rootElements);
		return isSubmitted;
	}

	public boolean deleteElement(DecisionKnowledgeElement elementToBeDeleted, ApplicationUser user) {
		if (!checkIfDataIsValid(elementToBeDeleted)) {
			return false;
		}

		List<DecisionKnowledgeElement> rootElements = getWebhookRootElements(elementToBeDeleted);
		String type = elementToBeDeleted.getTypeAsString();

		for (String rootType : rootTypes) {
			if (rootType.equalsIgnoreCase(type)) {
				rootElements.remove(elementToBeDeleted);
			}
		}

		AbstractPersistenceManagerForSingleLocation strategy = KnowledgePersistenceManager.getOrCreate(projectKey).getDefaultPersistenceManager();
		boolean isDeleted = strategy.deleteDecisionKnowledgeElement(elementToBeDeleted, user);
		if (isDeleted) {
			isDeleted = postKnowledgeTrees(rootElements);
		}
		return isDeleted;
	}

	private boolean postKnowledgeTrees(List<DecisionKnowledgeElement> rootElements) {
		for (DecisionKnowledgeElement rootElement : rootElements) {
			if (!postKnowledgeTree(rootElement)) {
				return false;
			}
		}
		return true;
	}

	private List<DecisionKnowledgeElement> getWebhookRootElements(DecisionKnowledgeElement element) {
		List<DecisionKnowledgeElement> webhookRootElements = new ArrayList<DecisionKnowledgeElement>();

		AbstractPersistenceManagerForSingleLocation strategy = KnowledgePersistenceManager.getOrCreate(projectKey).getDefaultPersistenceManager();
		List<DecisionKnowledgeElement> linkedElements = strategy.getAdjacentElements(element);
		linkedElements.add(element);
		for (DecisionKnowledgeElement linkedElement : linkedElements) {
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

	private boolean postKnowledgeTree(DecisionKnowledgeElement rootElement) {
		WebhookContentProvider provider = new WebhookContentProvider(projectKey, rootElement.getKey(), secret);
		PostMethod postMethod = provider.createPostMethod();
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

	private boolean checkIfDataIsValid(DecisionKnowledgeElement changedElement) {
		if (url == null || url.equals("")) {
			LOGGER.error("Could not trigger webhook data because the url is missing.");
			return false;
		}
		if (secret == null || secret.equals("")) {
			LOGGER.error("Could not trigger webhook data because the secret is missing.");
			return false;
		}
		if (projectKey == null || projectKey.equals("")) {
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
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
