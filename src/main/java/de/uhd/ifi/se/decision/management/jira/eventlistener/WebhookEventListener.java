package de.uhd.ifi.se.decision.management.jira.eventlistener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.webhook.WebhookConnector;

/**
 * Triggers the webhook when JIRA issues are created, updated, or deleted or
 * when links between JIRA issues are created or deleted
 */
public class WebhookEventListener {

	protected static final Logger LOGGER = LoggerFactory.getLogger(WebhookEventListener.class);

	public void onIssueEvent(IssueEvent issueEvent) {
		//System.out.println("WebhookListerner aufgelöst");
		String projectKey = issueEvent.getProject().getKey();
		if (!ConfigPersistenceManager.isWebhookEnabled(projectKey)) {
			System.out.println("isWebhookEnabled : FALSE");
			return;
		}
		long eventTypeId = issueEvent.getEventTypeId();
		KnowledgeElement decisionKnowledgeElement = new KnowledgeElement(issueEvent.getIssue());

		if (eventTypeId == EventType.ISSUE_CREATED_ID){
			LOGGER.info("WebhookListerner createdElement"+ decisionKnowledgeElement.getSummary());
			WebhookConnector connector = new WebhookConnector(projectKey);
				System.out.println("EventListener: gehe zu  WebhookConnector:" +projectKey);
			connector.sendnewElement(decisionKnowledgeElement);
		}
		if(eventTypeId == EventType.ISSUE_UPDATED_ID) {
			LOGGER.info("WebhookListerner sendetElementchange"+ decisionKnowledgeElement.getSummary());
			//System.out.println("WebhookListerner sendetElementchange"+ decisionKnowledgeElement.getSummary());
			WebhookConnector connector = new WebhookConnector(projectKey);
				System.out.println("EventListener: gehe zu  WebhookConnector:" +projectKey);
			connector.sendElementChanges(decisionKnowledgeElement);
		}
		if(eventTypeId == EventType.ISSUE_DELETED_ID) {
			System.out.println("deleted_id");
			WebhookConnector webhookConnector = new WebhookConnector(projectKey);
			webhookConnector.deleteElement(decisionKnowledgeElement, issueEvent.getUser());
		}
	}

	public void onLinkEvent(KnowledgeElement decisionKnowledgeElement) {
		if (!ConfigPersistenceManager.isWebhookEnabled(decisionKnowledgeElement.getProject().getProjectKey())) {
			return;
		}
		WebhookConnector connector = new WebhookConnector(decisionKnowledgeElement.getProject().getProjectKey());
		connector.sendElementChanges(decisionKnowledgeElement);
	}
}
