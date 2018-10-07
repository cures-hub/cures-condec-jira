package de.uhd.ifi.se.decision.management.jira.webhook;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpsURL;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.AbstractPersistenceStrategy;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistence;
import de.uhd.ifi.se.decision.management.jira.persistence.StrategyProvider;

/**
 * Webhook class that posts changed decision knowledge to a given URL.
 */
public class WebhookConnector {
	private static final Logger LOGGER = LoggerFactory.getLogger(WebhookConnector.class);
	private String url;
	private String secret;
	private String projectKey;
	private List<Long> elementIds;
	private String rootType;

	public WebhookConnector(String projectKey, String webhookUrl, String webhookSecret, String rootType) {
		this.projectKey = projectKey;
		this.url = webhookUrl;
		this.secret = webhookSecret;
		if (rootType == null) {
			this.rootType = "Task";
		} else {
			this.rootType = rootType;
		}
		this.elementIds = new ArrayList<Long>();
	}

	public WebhookConnector(String projectKey) {
		this(projectKey, ConfigPersistence.getWebhookUrl(projectKey), ConfigPersistence.getWebhookSecret(projectKey),
				ConfigPersistence.getWebhookType(projectKey));
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

	public boolean deleteElement(DecisionKnowledgeElement changedElement) {
		boolean isDeleted = false;
		if (!checkIfDataIsValid(changedElement)) {
			return isDeleted;
		}
		List<DecisionKnowledgeElement> rootElements = getWebhookRootElements(changedElement);
		if (changedElement.getType().toString().equals(rootType)) {
			rootElements.remove(changedElement);
		}
		isDeleted = postKnowledgeTrees(rootElements);
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

		AbstractPersistenceStrategy strategy = StrategyProvider.getPersistenceStrategy(projectKey);
		List<DecisionKnowledgeElement> linkedElements = strategy.getLinkedElements(element);
		for (DecisionKnowledgeElement linkedElement : linkedElements) {
			if (elementIds.contains(linkedElement.getId())) {
				continue;
			}
			elementIds.add(linkedElement.getId());
			if (linkedElement.getType().toString().equals(rootType)) {
				webhookRootElements.add(linkedElement);
			}
			webhookRootElements.addAll(getWebhookRootElements(linkedElement));
		}
		return webhookRootElements;
	}

	private boolean postKnowledgeTree(DecisionKnowledgeElement rootElement) {
		WebhookContentProvider provider = new WebhookContentProvider(rootElement.getKey(), secret);
		PostMethod postMethod = provider.createPostMethod();
		try {
			HttpClient httpClient = new HttpClient();
			postMethod.setURI(new HttpsURL(url));
			int httpResponse = httpClient.executeMethod(postMethod);
			if (httpResponse >= 200 && httpResponse < 300) {
				return true;
			}
		} catch (IOException e) {
			LOGGER.error("Could not send webhook data because of " + e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	private boolean checkIfDataIsValid(DecisionKnowledgeElement changedElement) {
		if (this.url == null || this.url.equals("")) {
			LOGGER.error("Could not trigger webhook data because the url is missing.");
			return false;
		}
		if (this.secret == null || this.secret.equals("")) {
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

	public void setSecret(String secret) {
		this.secret = secret;
	}
}
