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
import com.atlassian.jira.issue.comments.MutableComment;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;

import de.uhd.ifi.se.decision.management.jira.extraction.connector.ViewConnector;
import de.uhd.ifi.se.decision.management.jira.extraction.model.util.CommentSplitter;
import de.uhd.ifi.se.decision.management.jira.extraction.persistence.ActiveObjectsManager;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueCommentPersistenceManager;

/**
 * Triggers the decXtract related function when some changes to comments are
 * made.
 */
@Component
public class DecXtractEventListener implements InitializingBean, DisposableBean {

	@JiraImport
	private final EventPublisher eventPublisher;

	private String projectKey;

	private IssueEvent issueEvent;

	private static final Logger LOGGER = LoggerFactory.getLogger(DecXtractEventListener.class);

	/**
	 * Locks the editComment Event function if a rest service edites comments.
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
		LOGGER.debug("DecXtract Event Listener catched an event");
		this.issueEvent = issueEvent;
		this.projectKey = issueEvent.getProject().getKey();
		if (!ConfigPersistenceManager.isActivated(this.projectKey)
				&& !ConfigPersistenceManager.isKnowledgeExtractedFromIssues(this.projectKey)) {
			return;
		}
		Long eventTypeId = issueEvent.getEventTypeId();
		if (eventTypeId.equals(EventType.ISSUE_COMMENTED_ID) || eventTypeId.equals(EventType.ISSUE_COMMENT_EDITED_ID)) {

			LOGGER.debug("\nDecXtract Event Listener:\nparseIconsToTags()\ncreateLinksForNonLinkedElementsForIssue");
			parseIconsToTags();
			//ActiveObjectsManager.createLinksForNonLinkedElementsForIssue(
				//	new DecisionKnowledgeElementImpl(issueEvent.getIssue()).getId());
		}
		if (eventTypeId.equals(EventType.ISSUE_COMMENTED_ID)) {
			LOGGER.debug("\nDecXtract Event Listener:\nhandleNewComment()");
			handleNewComment(new DecisionKnowledgeElementImpl(issueEvent.getIssue()));
		}
		if (eventTypeId.equals(EventType.ISSUE_COMMENT_DELETED_ID)) {
			LOGGER.debug("\nDecXtract Event Listener:\nhandleDeleteComment()");
			handleDeleteComment(new DecisionKnowledgeElementImpl(issueEvent.getIssue()));

		}
		if (eventTypeId.equals(EventType.ISSUE_COMMENT_EDITED_ID)) {
			LOGGER.debug("\nDecXtract Event Listener:\nhandleEditComment()");
			handleEditComment(new DecisionKnowledgeElementImpl(issueEvent.getIssue()));
		}
		if (eventTypeId.equals(EventType.ISSUE_DELETED_ID)) {
			LOGGER.debug("\nDecXtract Event Listener:\nhandleDeleteIssue()");
			handleDeleteIssue(new DecisionKnowledgeElementImpl(issueEvent.getIssue()));
		}
		parseSlashTagsOutOfComment();
	}

	private void parseIconsToTags() {
		if (!ConfigPersistenceManager.isIconParsing(issueEvent.getProject().getKey())) {
			return;
		}
		MutableComment comment = getCurrentEditedComment();
		for (int i = 0; i < CommentSplitter.manualRationalIconList.length; i++) {
			String icon = CommentSplitter.manualRationalIconList[i];
			while (comment.getBody().contains(icon)) {
				comment.setBody(comment.getBody().replaceFirst(icon.replace("(", "\\(").replace(")", "\\)"),
						CommentSplitter.manualRationaleTagList[i]));
				if (comment.getBody().split(System.getProperty("line.separator")).length == 1
						&& !comment.getBody().endsWith("\r\n")) {
					comment.setBody(comment.getBody() + CommentSplitter.manualRationaleTagList[i]);
				}
				comment.setBody(comment.getBody().replaceFirst("\r\n", CommentSplitter.manualRationaleTagList[i]));
				ComponentAccessor.getCommentManager().update(comment, true);
			}
		}
	}

	/**
	 * Parses \{ out of tags. These slashes are inserted by the visual mode editor.
	 */
	private void parseSlashTagsOutOfComment() {
		if (issueEvent.getComment() != null && issueEvent.getComment().getBody().contains("\\{")) {
			MutableComment comment = getCurrentEditedComment();
			comment.setBody(issueEvent.getComment().getBody().replace("\\{", "{"));
			ComponentAccessor.getCommentManager().update(comment, true);
		}
	}

	private MutableComment getCurrentEditedComment() {
		return (MutableComment) ComponentAccessor.getCommentManager().getCommentById(issueEvent.getComment().getId());
	}

	private void handleDeleteIssue(DecisionKnowledgeElement decisionKnowledgeElement) {
		JiraIssueCommentPersistenceManager.cleanSentenceDatabaseForProject(this.projectKey);
		ActiveObjectsManager.createLinksForNonLinkedElementsForIssue(decisionKnowledgeElement.getId());
	}

	private void handleEditComment(DecisionKnowledgeElement decisionKnowledgeElement) {
		// If locked, a REST service is manipulating the comment and should not be
		// handled by this event listener.
		if (!DecXtractEventListener.editCommentLock) {
			ActiveObjectsManager.deleteCommentsSentences(issueEvent.getComment());
			new ViewConnector(this.issueEvent.getIssue(), false);
			ActiveObjectsManager.createLinksForNonLinkedElementsForIssue(decisionKnowledgeElement.getId());
		} else {
			LOGGER.debug("\nDecXtract Event Listener:\nHandle Edit Comment is still locked");
		}
	}

	private void handleDeleteComment(DecisionKnowledgeElement decisionKnowledgeElement) {
		JiraIssueCommentPersistenceManager.cleanSentenceDatabaseForProject(this.projectKey);
		ActiveObjectsManager.createLinksForNonLinkedElementsForIssue(decisionKnowledgeElement.getId());
	}

	private void handleNewComment(DecisionKnowledgeElement decisionKnowledgeElement) {
		new ViewConnector(this.issueEvent.getIssue(), false);
		ActiveObjectsManager.createLinksForNonLinkedElementsForIssue(decisionKnowledgeElement.getId());
	}

}
