package de.uhd.ifi.se.decision.management.jira.eventlistener;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.event.ProjectDeletedEvent;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.issue.link.IssueLinkCreatedEvent;
import com.atlassian.jira.event.issue.link.IssueLinkDeletedEvent;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.eventlistener.implementation.JiraIssueChangeListener;
import de.uhd.ifi.se.decision.management.jira.eventlistener.implementation.JiraIssueTextExtractionEventListener;
import de.uhd.ifi.se.decision.management.jira.eventlistener.implementation.QualityCheckEventListenerSingleton;
import de.uhd.ifi.se.decision.management.jira.eventlistener.implementation.SummarizationEventListener;
import de.uhd.ifi.se.decision.management.jira.eventlistener.implementation.WebhookEventListener;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.Link;

/**
 * This class is responsible for all the event listeners used in this plug-in.
 *
 * @see JiraIssueTextExtractionEventListener
 * @see SummarizationEventListener
 * @see WebhookEventListener
 */
@Scanned
@Component
public class ConDecEventListener implements InitializingBean, DisposableBean {

	@JiraImport
	private final EventPublisher eventPublisher;
	private List<IssueEventListener> issueEventListeners;
	private List<LinkEventListener> linkEventListeners;
	private List<ProjectEventListener> projectEventListeners;

	protected static final Logger LOGGER = LoggerFactory.getLogger(ConDecEventListener.class);

	@Autowired
	@Inject
	public ConDecEventListener(EventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
		LOGGER.info("ConDec event listener object was created.");
		// init lists of listeners
		issueEventListeners = new ArrayList<>();
		linkEventListeners = new ArrayList<>();
		projectEventListeners = new ArrayList<>();

		// attach Jira issue event listeners
		issueEventListeners.add(new WebhookEventListener());
		issueEventListeners.add(new JiraIssueTextExtractionEventListener());
		issueEventListeners.add(new SummarizationEventListener());
		issueEventListeners.add(QualityCheckEventListenerSingleton.getInstance());
		issueEventListeners.add(new JiraIssueChangeListener());

		// attach Jira issue link event listeners
		linkEventListeners.add(new WebhookEventListener());

		// attach Jira project deleted event listeners
		projectEventListeners.add(new JiraIssueTextExtractionEventListener());
	}

	/**
	 * Called when the plugin has been enabled.
	 */
	@Override
	public void afterPropertiesSet() {
		eventPublisher.register(this);
		LOGGER.info("ConDec event listener was registered in Jira.");
	}

	/**
	 * Called when the plugin is being disabled or removed.
	 */
	@Override
	public void destroy() {
		eventPublisher.unregister(this);
		LOGGER.info("ConDec event listener was removed from Jira.");
	}

	@EventListener
	public void onIssueEvent(IssueEvent issueEvent) {
		if (issueEvent == null) {
			return;
		}
		this.issueEventListeners.forEach(issueEventListener -> issueEventListener.onIssueEvent(issueEvent));
	}

	@EventListener
	public void onLinkCreatedIssueEvent(IssueLinkCreatedEvent linkCreatedEvent) {
		KnowledgeElement element = new KnowledgeElement(linkCreatedEvent.getIssueLink().getSourceObject());
		Link link = new Link(linkCreatedEvent.getIssueLink());
		KnowledgeGraph.getOrCreate(element.getProject()).removeEdge(link);
		KnowledgeGraph.getOrCreate(element.getProject()).addEdge(link);
		linkEventListeners.forEach(linkEventListener -> linkEventListener.onLinkEvent(element));
		LOGGER.info("ConDec event listener on link created issue event was triggered.");
	}

	@EventListener
	public void onLinkDeletedIssueEvent(IssueLinkDeletedEvent linkDeletedEvent) {
		KnowledgeElement element = new KnowledgeElement(linkDeletedEvent.getIssueLink().getSourceObject());
		Link link = new Link(linkDeletedEvent.getIssueLink());
		KnowledgeGraph.getOrCreate(element.getProject()).removeEdge(link);
		linkEventListeners.forEach(linkEventListener -> linkEventListener.onLinkEvent(element));
		LOGGER.info("ConDec event listener on link deleted issue event was triggered.");
	}

	@EventListener
	public void onProjectDeletedEvent(ProjectDeletedEvent projectDeletedEvent) {
		if (projectDeletedEvent == null) {
			return;
		}
		this.projectEventListeners
				.forEach(projectEventListener -> projectEventListener.onProjectDeletion(projectDeletedEvent));
		ComponentGetter.removeInstances(projectDeletedEvent.getProject().getKey());
	}
}
