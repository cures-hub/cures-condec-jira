package de.uhd.ifi.se.decision.management.jira.webhook;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.AbstractPersistenceStrategy;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistence;
import de.uhd.ifi.se.decision.management.jira.persistence.StrategyProvider;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WebhookConnector {
	private static final Logger LOGGER = LoggerFactory.getLogger(WebhookConnector.class);
	private String url;
	private String secret;
	private String projectKey;
	private List<Long> elementIds;
	private String rootType;

	public WebhookConnector(String projectKey, String webhookUrl, String webhookSecret, String rootType) {
		if (projectKey == null) {
			LOGGER.error("Webhook could not be created because the project key is not provided.");
			projectKey = "";
		}
		if (webhookUrl == null) {
			webhookUrl = "";
			LOGGER.error("Webhook could not be created because the URL is not provided.");
		}
		if (webhookSecret == null) {
			webhookSecret = "";
			LOGGER.error("Webhook could not be created because the secret is not provided.");
		}
		if(rootType == null){
			rootType = "WorkItem";
		}
		this.projectKey = projectKey;
		this.url = webhookUrl;
		this.secret = webhookSecret;
		this.rootType = rootType;
	}

	public WebhookConnector(String projectKey) {
		this(projectKey, ConfigPersistence.getWebhookUrl(projectKey), ConfigPersistence.getWebhookSecret(projectKey),
				ConfigPersistence.getWebhookType(projectKey));
	}

	public boolean sendElementChanges(DecisionKnowledgeElement decisionKnowledgeElement, boolean isDeleted) {
		if (decisionKnowledgeElement == null) {
			LOGGER.error("Webhook could not be created because the Element is null");
			return false;
		}
		ArrayList<DecisionKnowledgeElement> workItems = getWebhookRootElements(decisionKnowledgeElement);
		if (isDeleted && decisionKnowledgeElement.getType().toString().equals(rootType)) {
			workItems.remove(decisionKnowledgeElement);
		}
		boolean submitted = true;
		for (DecisionKnowledgeElement workItem : workItems) {
			if (postKnowledge(projectKey, workItem.getKey())) {
				submitted = false;
			}
		}
		return submitted;
	}

	public boolean sendElementChanges(DecisionKnowledgeElement decisionKnowledgeElement) {
		return sendElementChanges(decisionKnowledgeElement, false);
	}

	private ArrayList<DecisionKnowledgeElement> getWebhookRootElements(DecisionKnowledgeElement element) {
		ArrayList<DecisionKnowledgeElement> webhookRootElements = new ArrayList<DecisionKnowledgeElement>();

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

	private boolean postKnowledge(String projectKey, String changedElementKey) {
		if (!checkIfDataIsValid(projectKey, changedElementKey)) {
			return false;
		}
		WebhookContentProvider provider = new WebhookContentProvider(projectKey, changedElementKey);
		PostMethod postMethod = provider.createWebhookContentForChangedElement();
		boolean isSubmitted = submitPostMethod(postMethod);
		return isSubmitted;
	}

	private boolean checkIfDataIsValid(String projectKey, String changedElementkey) {
		if (this.url == null || this.url.equals("")) {
			LOGGER.error("Could not send WebHook data because the Url is Null or empty");
			return false;
		}
		if (this.secret == null || this.secret.equals("")) {
			LOGGER.error("Could not send WebHook data because Secret is Null or empty");
			return false;
		}
		if (projectKey == null || projectKey.equals("")) {
			LOGGER.error("Could not send WebHook data because projectKey Null or empty");
			return false;
		}
		if (changedElementkey == null || changedElementkey.equals("")) {
			LOGGER.error("Could not send WebHook data because issueKey Null or empty");
			return false;
		}
		return true;
	}

	private boolean submitPostMethod(PostMethod postMethod) {
		try {
			HttpClient httpClient = new HttpClient();
			postMethod.setURI(new HttpsURL(url));
			int respEntity = httpClient.executeMethod(postMethod);
			if (respEntity >= 200 && respEntity < 300) {
				return true;
			}
		} catch (IOException e) {
			LOGGER.error("Could not send webhook data because of " + e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}
}
