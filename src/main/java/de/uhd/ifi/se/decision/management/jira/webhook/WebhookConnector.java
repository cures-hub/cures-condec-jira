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

import java.io.*;

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
	private String receiver;


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

		this.receiver  = "Other";
		if (this.url != null){
			if(url.matches("https://hooks.slack.com(\\S*)")) {
			this.receiver = "Slack";}
		}


	}

	public WebhookConnector(String projectKey) {
		this(projectKey,
				ConfigPersistenceManager.getWebhookUrl(projectKey),
				ConfigPersistenceManager.getWebhookSecret(projectKey),
				ConfigPersistenceManager.getEnabledWebhookTypes(projectKey));
	}

	public boolean sendElementChanges(KnowledgeElement changedElement) {
		//System.out.println("sendElementChanges. Receiver: "+this.receiver);
		boolean isSubmitted = false;
		if (!checkIfDataIsValid(changedElement)) {
			return isSubmitted;
		}
		if(this.receiver == "Other"){
			List<KnowledgeElement> rootElements = getWebhookRootElements(changedElement);
			isSubmitted = postKnowledgeTrees(rootElements);
			return isSubmitted;
	  }
		if(this.receiver == "Slack"){
			System.out.println("sendElementChanges, receiver : Slack, Element:"+changedElement.getSummary());
			isSubmitted = postKnowledgeElement(changedElement);
			System.out.println(isSubmitted);
			return isSubmitted;
		}
		return false;
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
				.deleteDecisionKnowledgeElement(elementToBeDeleted, user);
		if (isDeleted) {
			isDeleted = postKnowledgeTrees(rootElements);
		}
		return isDeleted;
	}

	private boolean postKnowledgeTrees(List<KnowledgeElement> rootElements) {
		for (KnowledgeElement rootElement : rootElements) {
			if (!postKnowledgeTree(rootElement)) {
				return false;
			}
		}
		return true;
	}

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

	private boolean postKnowledgeTree(KnowledgeElement rootElement) {
		WebhookContentProvider provider = new WebhookContentProvider(projectKey, rootElement, secret, receiver);
		PostMethod postMethod = provider.createPostMethod();

		try {
			HttpClient httpClient = new HttpClient();
			postMethod.setURI(new HttpsURL(url));
			System.out.println(postMethod.toString());
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


	private boolean postKnowledgeElement(KnowledgeElement changedElement) {
		System.out.println("postKnowledgeElement");
		WebhookContentProvider provider = new WebhookContentProvider(projectKey, changedElement, secret, receiver);
		PostMethod postMethod = provider.createPostMethodForSlack(changedElement);
		System.out.println(postMethod.toString());
		try {
			HttpClient httpClient = new HttpClient();
			postMethod.setURI(new HttpsURL(url));
			int httpResponse = httpClient.executeMethod(postMethod);
			if (httpResponse >= 200 && httpResponse < 300) {
				return true;
			}
			LOGGER.error("Could not send webhook data. The HTTP response code is: " + httpResponse);
			//LOGGER.error("Could not send webhook data. The HTTP response code is: " + process);
		} catch (IOException | IllegalArgumentException e) {
			LOGGER.error("Could not send webhook data because of " + e.getMessage());
		}
		return false;
	}

	private boolean checkIfDataIsValid(KnowledgeElement changedElement) {
		if (url == null || url.equals("")) {
			LOGGER.error("Could not trigger webhook data because the url is missing.");
			return false;
		}
		if ((secret == null || secret.equals("")) && receiver != "Slack") {
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
		return this.url;
	}
	public String getReceiver() {
		return this.receiver;
	}
	public void setUrl(String url) {
		this.url = url;

		if (this.url != null){
			if(url.matches("https://hooks.slack.com(\\S*)")) {
			this.receiver = "Slack";}
		}else{
			this.receiver  = "Other";
		}

	}
}
