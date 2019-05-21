package de.uhd.ifi.se.decision.management.jira.webhook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.issue.link.IssueLinkCreatedEvent;
import com.atlassian.jira.event.issue.link.IssueLinkDeletedEvent;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;

/**
 * Triggers the webhook when JIRA issues are created, updated, or deleted or
 * when links between JIRA issues are created or deleted
 */
@Component
public class WebhookEventListener implements InitializingBean, DisposableBean {

	@JiraImport
	private final EventPublisher eventPublisher;

	protected static final Logger LOGGER = LoggerFactory.getLogger(WebhookEventListener.class);

	@Autowired
	public WebhookEventListener(EventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
		LOGGER.info("Webhook Eventlistener was added to JIRA");
	}

	/**
	 * Called when the plugin has been enabled.
	 * 
	 * @throws Exception
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		eventPublisher.register(this);
		LOGGER.info("Webhook Eventlistener was added to JIRA");
	}

	/**
	 * Called when the plugin is being disabled or removed.
	 * 
	 * @throws Exception
	 */
	@Override
	public void destroy() throws Exception {
		eventPublisher.unregister(this);
		LOGGER.info("Webhook Eventlistener was removed from JIRA");
	}

	@EventListener
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

	@EventListener
	public void onLinkCreatedIssueEvent(IssueLinkCreatedEvent linkCreatedEvent) {
		String projectKey = linkCreatedEvent.getIssueLink().getSourceObject().getProjectObject().getKey();
		DecisionKnowledgeElement decisionKnowledgeElement = new DecisionKnowledgeElementImpl(
				linkCreatedEvent.getIssueLink().getSourceObject());
		if (!ConfigPersistenceManager.isWebhookEnabled(projectKey)) {
			return;
		}
		WebhookConnector connector = new WebhookConnector(projectKey);
		connector.sendElementChanges(decisionKnowledgeElement);
	}

	@EventListener
	public void onLinkDeletedIssueEvent(IssueLinkDeletedEvent linkDeletedEvent) {
		String projectKey = linkDeletedEvent.getIssueLink().getSourceObject().getProjectObject().getKey();
		DecisionKnowledgeElement decisionKnowledgeElement = new DecisionKnowledgeElementImpl(
				linkDeletedEvent.getIssueLink().getSourceObject());
		if (!ConfigPersistenceManager.isWebhookEnabled(projectKey)) {
			return;
		}
		WebhookConnector connector = new WebhookConnector(projectKey);
		connector.sendElementChanges(decisionKnowledgeElement);
	}
}
