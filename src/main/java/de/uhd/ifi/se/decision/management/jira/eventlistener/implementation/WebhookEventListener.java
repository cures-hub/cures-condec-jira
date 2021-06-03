package de.uhd.ifi.se.decision.management.jira.eventlistener.implementation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;

import de.uhd.ifi.se.decision.management.jira.eventlistener.IssueEventListener;
import de.uhd.ifi.se.decision.management.jira.eventlistener.LinkEventListener;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.webhook.WebhookConfiguration;
import de.uhd.ifi.se.decision.management.jira.webhook.WebhookConnector;

/**
 * Triggers the webhook when Jira issues are created, updated, or deleted or
 * when links between Jira issues are created or deleted.
 * 
 * @see WebhookConfiguration
 * @see WebhookConnector
 */
public class WebhookEventListener implements IssueEventListener, LinkEventListener {

	protected static final Logger LOGGER = LoggerFactory.getLogger(WebhookEventListener.class);

	public void onIssueEvent(IssueEvent issueEvent) {
		String projectKey = issueEvent.getProject().getKey();
		if (!ConfigPersistenceManager.getWebhookConfiguration(projectKey).isActivated()) {
			return;
		}
		long eventTypeId = issueEvent.getEventTypeId();
		KnowledgeElement decisionKnowledgeElement = new KnowledgeElement(issueEvent.getIssue());

		if (eventTypeId == EventType.ISSUE_CREATED_ID) {
			LOGGER.info("WebhookListerner createdElement" + decisionKnowledgeElement.getSummary());
			WebhookConnector connector = new WebhookConnector(projectKey);
			connector.sendElement(decisionKnowledgeElement, "new");
		}
		if (eventTypeId == EventType.ISSUE_UPDATED_ID) {
			LOGGER.info("WebhookListerner sendetElementchange" + decisionKnowledgeElement.getSummary());
			WebhookConnector connector = new WebhookConnector(projectKey);
			connector.sendElement(decisionKnowledgeElement, "changed");
		}
		if (eventTypeId == EventType.ISSUE_DELETED_ID) {
			WebhookConnector webhookConnector = new WebhookConnector(projectKey);
			webhookConnector.deleteElement(decisionKnowledgeElement, issueEvent.getUser());
		}
	}

	public void onLinkEvent(KnowledgeElement decisionKnowledgeElement) {
		if (!ConfigPersistenceManager.getWebhookConfiguration(decisionKnowledgeElement.getProject().getProjectKey())
				.isActivated()) {
			return;
		}
		WebhookConnector connector = new WebhookConnector(decisionKnowledgeElement.getProject().getProjectKey());
		connector.sendElement(decisionKnowledgeElement, "changed");
	}
}
