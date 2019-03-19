package de.uhd.ifi.se.decision.management.jira.extraction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.MutableComment;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;

import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.text.TextSplitter;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueCommentPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssuePersistenceManager;

/**
 * Triggers the extraction of decision knowledge elements and their integration
 * in the knowledge graph when the user changes either a comment or the
 * description of a JIRA issue.
 */
@Component
public class DecXtractEventListener implements InitializingBean, DisposableBean {

	@JiraImport
	private final EventPublisher eventPublisher;
	private String projectKey;
	private IssueEvent issueEvent;
	private static final Logger LOGGER = LoggerFactory.getLogger(DecXtractEventListener.class);

	/**
	 * Locks the edit comment event function if a REST service edits comments.
	 */
	public static boolean editCommentLock;

	@Autowired
	public DecXtractEventListener(EventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
	}

	/**
	 * Called when the plugin has been enabled.
	 * 
	 * @throws Exception
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		eventPublisher.register(this);
	}

	/**
	 * Called when the plugin is being disabled or removed.
	 * 
	 * @throws Exception
	 */
	@Override
	public void destroy() throws Exception {
		eventPublisher.unregister(this);
	}

	@EventListener
	public void onIssueEvent(IssueEvent issueEvent) {
		if (issueEvent == null) {
			return;
		}
		this.issueEvent = issueEvent;
		this.projectKey = issueEvent.getProject().getKey();

		if (!ConfigPersistenceManager.isActivated(this.projectKey)
				&& !ConfigPersistenceManager.isKnowledgeExtractedFromIssues(this.projectKey)) {
			return;
		}

		long eventTypeId = issueEvent.getEventTypeId();
		if (eventTypeId == EventType.ISSUE_COMMENTED_ID || eventTypeId == EventType.ISSUE_COMMENT_EDITED_ID) {
			parseIconsToTags();
		}
		if (eventTypeId == EventType.ISSUE_COMMENTED_ID) {
			handleNewComment();
		}
		if (eventTypeId == EventType.ISSUE_COMMENT_DELETED_ID) {
			handleDeleteComment();
		}
		if (eventTypeId == EventType.ISSUE_COMMENT_EDITED_ID) {
			handleEditComment();
		}
		if (eventTypeId == EventType.ISSUE_DELETED_ID) {
			handleDeleteIssue();
		}
		if (eventTypeId == EventType.ISSUE_UPDATED_ID) {
			handleUpdateDescription();
		}
	}

	private void parseIconsToTags() {
		String projectKey = issueEvent.getProject().getKey();
		if (!ConfigPersistenceManager.isIconParsing(projectKey)) {
			return;
		}
		MutableComment comment = (MutableComment) issueEvent.getComment();
		if (comment == null) {
			Issue jiraIssue = issueEvent.getIssue();
			String description = jiraIssue.getDescription();
			description = TextSplitter.parseIconsToTags(description);
			new JiraIssuePersistenceManager(issueEvent.getProject().getKey())
					.updateDecisionKnowledgeElement(new DecisionKnowledgeElementImpl(jiraIssue), issueEvent.getUser());
		} else {
			String commentBody = comment.getBody();
			commentBody = TextSplitter.parseIconsToTags(commentBody);
			comment.setBody(commentBody);
			ComponentAccessor.getCommentManager().update(comment, true);
		}
	}

	private void handleDeleteIssue() {
		JiraIssueCommentPersistenceManager.cleanSentenceDatabase(projectKey);
		JiraIssueCommentPersistenceManager.createLinksForNonLinkedElementsForIssue(issueEvent.getIssue().getId());
	}

	private void handleEditComment() {
		if (DecXtractEventListener.editCommentLock) {
			// If locked, a REST service is currently manipulating the comment and should
			// not be handled by this event listener.
			LOGGER.debug("DecXtract event listener:\nEditing comment is still locked.");
			return;
		}
		JiraIssueCommentPersistenceManager.deleteAllSentencesOfComments(issueEvent.getComment());
		if (ConfigPersistenceManager.isUseClassiferForIssueComments(this.projectKey)) {
			new ClassificationManagerForJiraIssueComments().classifyAllCommentsOfJiraIssue(this.issueEvent.getIssue());
		} else {
			MutableComment comment = (MutableComment) issueEvent.getComment();
			JiraIssueCommentPersistenceManager.getPartsOfComment(comment);
		}
		JiraIssueCommentPersistenceManager.createLinksForNonLinkedElementsForIssue(issueEvent.getIssue().getId());
	}

	private void handleDeleteComment() {
		JiraIssueCommentPersistenceManager.cleanSentenceDatabase(this.projectKey);
		JiraIssueCommentPersistenceManager.createLinksForNonLinkedElementsForIssue(issueEvent.getIssue().getId());
	}

	private void handleNewComment() {
		if (ConfigPersistenceManager.isUseClassiferForIssueComments(this.projectKey)) {
			new ClassificationManagerForJiraIssueComments().classifyAllCommentsOfJiraIssue(this.issueEvent.getIssue());
		} else {
			MutableComment comment = (MutableComment) issueEvent.getComment();
			JiraIssueCommentPersistenceManager.getPartsOfComment(comment);
		}
		JiraIssueCommentPersistenceManager.createLinksForNonLinkedElementsForIssue(issueEvent.getIssue().getId());
	}

	private void handleUpdateDescription() {
		// If locked, a REST service is currently manipulating the comment and should
		// not be handled by this event listener.
		if (!DecXtractEventListener.editCommentLock) {
			if (ConfigPersistenceManager.isUseClassiferForIssueComments(this.projectKey)) {
				new ClassificationManagerForJiraIssueComments()
						.classifyAllCommentsOfJiraIssue(this.issueEvent.getIssue());
			} else {
				JiraIssueCommentPersistenceManager.getPartsOfDescription(issueEvent.getIssue());
			}
			JiraIssueCommentPersistenceManager.createLinksForNonLinkedElementsForIssue(issueEvent.getIssue().getId());
		} else {
			LOGGER.debug("DecXtract event listener:\nEditing description is still locked.");
		}
	}
}
