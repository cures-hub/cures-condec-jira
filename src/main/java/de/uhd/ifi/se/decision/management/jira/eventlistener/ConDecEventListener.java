package de.uhd.ifi.se.decision.management.jira.eventlistener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.issue.link.IssueLinkCreatedEvent;
import com.atlassian.jira.event.issue.link.IssueLinkDeletedEvent;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;

import aQute.bnd.annotation.component.Component;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;

@Component
public class ConDecEventListener implements InitializingBean, DisposableBean {

	@JiraImport
	private final EventPublisher eventPublisher;
	private WebhookEventListener webhookEventListener;
	private JiraIssueTextExtractionEventListener jiraIssueTextExtractionEventListener;
	private SummarizationEventListener summarizationEventListener;

	protected static final Logger LOGGER = LoggerFactory.getLogger(ConDecEventListener.class);

	@Autowired
	public ConDecEventListener(EventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
		LOGGER.info("ConDec event listener was added to JIRA");
		webhookEventListener = new WebhookEventListener();
		jiraIssueTextExtractionEventListener = new JiraIssueTextExtractionEventListener();
		summarizationEventListener = new SummarizationEventListener();
	}

	/**
	 * Called when the plugin has been enabled.
	 *
	 * @throws Exception
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		eventPublisher.register(this);
		LOGGER.info("ConDec event listener was added to JIRA.");
	}

	/**
	 * Called when the plugin is being disabled or removed.
	 *
	 * @throws Exception
	 */
	@Override
	public void destroy() throws Exception {
		eventPublisher.unregister(this);
		LOGGER.info("ConDec event listener was removed from JIRA.");
	}

	@EventListener
	public void onIssueEvent(IssueEvent issueEvent) {
		if (issueEvent == null) {
			return;
		}
		webhookEventListener.onIssueEvent(issueEvent);
		jiraIssueTextExtractionEventListener.onIssueEvent(issueEvent);
		summarizationEventListener.onIssueEvent(issueEvent);
	}

	@EventListener
	public void onLinkCreatedIssueEvent(IssueLinkCreatedEvent linkCreatedEvent) {
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl(
				linkCreatedEvent.getIssueLink().getSourceObject());
		webhookEventListener.onLinkEvent(element);
		LOGGER.info("ConDec event listener on link create issue event triggered.");
	}

	@EventListener
	public void onLinkDeletedIssueEvent(IssueLinkDeletedEvent linkDeletedEvent) {
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl(
				linkDeletedEvent.getIssueLink().getSourceObject());
		webhookEventListener.onLinkEvent(element);
		LOGGER.info("ConDec event listener on link deleted issue event triggered.");
	}
}
