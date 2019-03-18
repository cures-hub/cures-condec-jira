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

import de.uhd.ifi.se.decision.management.jira.extraction.impl.TextSplitterImpl;
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
		parseSlashTagsOutOfComment();
	}

	private void parseIconsToTags() {
		if (!ConfigPersistenceManager.isIconParsing(issueEvent.getProject().getKey())) {
			return;
		}
		MutableComment comment = getChangedComment();
		for (int i = 0; i < TextSplitter.RATIONALE_ICONS.length; i++) {
			String icon = TextSplitter.RATIONALE_ICONS[i];
			while (comment.getBody().contains(icon)) {
				comment.setBody(comment.getBody().replaceFirst(icon.replace("(", "\\(").replace(")", "\\)"),
						TextSplitter.RATIONALE_TAGS[i]));
				if (comment.getBody().split(System.getProperty("line.separator")).length == 1
						&& !comment.getBody().endsWith("\r\n")) {
					comment.setBody(comment.getBody() + TextSplitter.RATIONALE_TAGS[i]);
				}
				comment.setBody(comment.getBody().replaceFirst("\r\n", TextSplitter.RATIONALE_TAGS[i]));
				ComponentAccessor.getCommentManager().update(comment, true);
			}
		}
	}

	/**
	 * Parses \{ out of tags. These slashes are inserted by the visual mode editor.
	 */
	private void parseSlashTagsOutOfComment() {
		if (issueEvent.getComment() != null && issueEvent.getComment().getBody().contains("\\{")) {
			MutableComment comment = getChangedComment();
			comment.setBody(issueEvent.getComment().getBody().replace("\\{", "{"));
			ComponentAccessor.getCommentManager().update(comment, true);
		}
	}

	private MutableComment getChangedComment() {
		return (MutableComment) ComponentAccessor.getCommentManager().getCommentById(issueEvent.getComment().getId());
	}

	private void handleDeleteIssue() {
		JiraIssueCommentPersistenceManager.cleanSentenceDatabaseForProject(this.projectKey);
		JiraIssueCommentPersistenceManager.createLinksForNonLinkedElementsForIssue(issueEvent.getIssue().getId());
	}

	private void handleEditComment() {
		// If locked, a REST service is currently manipulating the comment and should
		// not be handled by this event listener.
		if (!DecXtractEventListener.editCommentLock) {
			JiraIssueCommentPersistenceManager.deleteAllSentencesOfComments(issueEvent.getComment());
			if (ConfigPersistenceManager.isUseClassiferForIssueComments(this.projectKey)) {
				new ClassificationManagerForJiraIssueComments()
						.classifyAllCommentsOfJiraIssue(this.issueEvent.getIssue());
			} else {
				MutableComment comment = getChangedComment();
				new TextSplitterImpl().getPartsOfComment(comment);
			}
			JiraIssueCommentPersistenceManager.createLinksForNonLinkedElementsForIssue(issueEvent.getIssue().getId());
		} else {
			LOGGER.debug("DecXtract event listener:\nEditing comment is still locked.");
		}
	}

	private void handleDeleteComment() {
		JiraIssueCommentPersistenceManager.cleanSentenceDatabaseForProject(this.projectKey);
		JiraIssueCommentPersistenceManager.createLinksForNonLinkedElementsForIssue(issueEvent.getIssue().getId());
	}

	private void handleNewComment() {
		if (ConfigPersistenceManager.isUseClassiferForIssueComments(this.projectKey)) {
			new ClassificationManagerForJiraIssueComments().classifyAllCommentsOfJiraIssue(this.issueEvent.getIssue());
		} else {
			MutableComment comment = getChangedComment();
			new TextSplitterImpl().getPartsOfComment(comment);
		}
		JiraIssueCommentPersistenceManager.createLinksForNonLinkedElementsForIssue(issueEvent.getIssue().getId());
	}
}
