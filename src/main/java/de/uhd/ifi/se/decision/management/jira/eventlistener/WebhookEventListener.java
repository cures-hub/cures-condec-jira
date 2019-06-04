package de.uhd.ifi.se.decision.management.jira.eventlistener;

import de.uhd.ifi.se.decision.management.jira.webhook.WebhookConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;

/**
 * Triggers the webhook when JIRA issues are created, updated, or deleted or
 * when links between JIRA issues are created or deleted
 */
public class WebhookEventListener{


	protected static final Logger LOGGER = LoggerFactory.getLogger(WebhookEventListener.class);

	public void onIssueEvent(IssueEvent issueEvent) {
		String projectKey = issueEvent.getProject().getKey();
		if (!ConfigPersistenceManager.isWebhookEnabled(projectKey)) {
			return;
		}
		long eventTypeId = issueEvent.getEventTypeId();
		DecisionKnowledgeElement decisionKnowledgeElement = new DecisionKnowledgeElementImpl(issueEvent.getIssue());
		if (eventTypeId == EventType.ISSUE_CREATED_ID || eventTypeId == EventType.ISSUE_UPDATED_ID) {
			WebhookConnector connector = new WebhookConnector(projectKey);
			connector.sendElementChanges(decisionKnowledgeElement);
		}
		if (eventTypeId == EventType.ISSUE_DELETED_ID) {
			WebhookConnector webhookConnector = new WebhookConnector(projectKey);
			webhookConnector.deleteElement(decisionKnowledgeElement, issueEvent.getUser());
		}
	}

	public void onLinkEvent(DecisionKnowledgeElement decisionKnowledgeElement){
		if(!ConfigPersistenceManager.isWebhookEnabled(decisionKnowledgeElement.getProject().getProjectKey())){
			return;
		}
		WebhookConnector connector = new WebhookConnector(decisionKnowledgeElement.getProject().getProjectKey());
		connector.sendElementChanges(decisionKnowledgeElement);
	}
}
