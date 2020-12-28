package de.uhd.ifi.se.decision.management.jira.eventlistener.implementation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.event.ProjectDeletedEvent;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.comments.MutableComment;

import de.uhd.ifi.se.decision.management.jira.classification.implementation.ClassificationManagerForJiraIssueComments;
import de.uhd.ifi.se.decision.management.jira.eventlistener.IssueEventListener;
import de.uhd.ifi.se.decision.management.jira.eventlistener.ProjectEventListener;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssuePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssueTextPersistenceManager;

/**
 * Triggers the extraction of decision knowledge elements and their integration
 * in the {@link KnowledgeGraph} when the user changes either a comment or the
 * description of a Jira issue. Is also responsible for deleting the elements in
 * database and the knowledge graph after a comment, a Jira issue, or an entire
 * project has been deleted.
 */
public class JiraIssueTextExtractionEventListener implements IssueEventListener, ProjectEventListener {

	private String projectKey;
	private IssueEvent issueEvent;
	private ClassificationManagerForJiraIssueComments classificationManagerForJiraIssueComments;
	private static final Logger LOGGER = LoggerFactory.getLogger(JiraIssueTextExtractionEventListener.class);

	public JiraIssueTextExtractionEventListener(
			ClassificationManagerForJiraIssueComments classificationManagerForJiraIssueComments) {
		this.classificationManagerForJiraIssueComments = classificationManagerForJiraIssueComments;
	}

	public JiraIssueTextExtractionEventListener() {
		this.classificationManagerForJiraIssueComments = new ClassificationManagerForJiraIssueComments();
	}

	/**
	 * Locks the edit comment event function if a REST service edits comments.
	 */
	public static boolean editCommentLock;

	/**
	 * @issue How to implement the handling of the concrete event?
	 * @decision Use if-statements to handle the concrete event!
	 * @alternative Use switch-case-statement to handle the concrete event!
	 * @con Switch-case-statement is not trivial to implement. The event type is a
	 *      long variable but the switch cannot be a long variable. Casting from
	 *      long to int is dangerous.
	 */
	@Override
	public void onIssueEvent(IssueEvent issueEvent) {
		this.issueEvent = issueEvent;
		this.projectKey = issueEvent.getProject().getKey();

		if (!ConfigPersistenceManager.isActivated(projectKey)) {
			return;
		}

		long eventTypeId = issueEvent.getEventTypeId();
		if (eventTypeId == EventType.ISSUE_COMMENT_DELETED_ID) {
			handleDeleteComment();
		}
		if (eventTypeId == EventType.ISSUE_COMMENTED_ID || eventTypeId == EventType.ISSUE_COMMENT_EDITED_ID) {
			handleNewOrUpdatedComment();
		}
		if (eventTypeId == EventType.ISSUE_DELETED_ID) {
			handleDeleteIssue();
		}
		if (eventTypeId == EventType.ISSUE_UPDATED_ID || eventTypeId == EventType.ISSUE_CREATED_ID) {
			handleNewJiraIssueOrUpdatedDescription();
		}
	}

	private void handleDeleteIssue() {
		JiraIssueTextPersistenceManager persistenceManager = KnowledgePersistenceManager.getOrCreate(projectKey)
				.getJiraIssueTextManager();
		persistenceManager.deleteInvalidElements(issueEvent.getUser());
		KnowledgeElement element = new KnowledgeElement(issueEvent.getIssue());
		KnowledgeGraph.getOrCreate(element.getProject().getProjectKey()).removeVertex(element);
	}

	private void handleDeleteComment() {
		JiraIssueTextPersistenceManager persistenceManager = KnowledgePersistenceManager.getOrCreate(projectKey)
				.getJiraIssueTextManager();
		persistenceManager.deleteInvalidElements(issueEvent.getUser());
	}

	private void handleNewOrUpdatedComment() {
		if (JiraIssueTextExtractionEventListener.editCommentLock) {
			// If locked, a REST service is currently manipulating a comment or the
			// description and this event should not be handled by this event listener.
			LOGGER.debug("DecXtract event listener:\nEditing comment is still locked.");
			return;
		}

		replaceIconsWithTags();

		JiraIssueTextPersistenceManager persistenceManager = KnowledgePersistenceManager.getOrCreate(projectKey)
				.getJiraIssueTextManager();

		if (ConfigPersistenceManager.isClassifierEnabled(projectKey)) {
			persistenceManager.deleteElementsInComment(issueEvent.getComment());
			classificationManagerForJiraIssueComments.classifyComment(issueEvent.getComment());
		} else {
			MutableComment comment = (MutableComment) issueEvent.getComment();
			persistenceManager.updateElementsOfCommentInDatabase(comment);
		}
		persistenceManager.createLinksForNonLinkedElements(issueEvent.getIssue());
	}

	private void handleNewJiraIssueOrUpdatedDescription() {
		if (JiraIssueTextExtractionEventListener.editCommentLock) {
			// If locked, a REST service is currently manipulating a comment or the
			// description and this event should not be handled by this event listener.
			LOGGER.debug("DecXtract event listener:\nEditing description is still locked.");
			return;
		}

		replaceIconsWithTags();

		JiraIssueTextPersistenceManager persistenceManager = KnowledgePersistenceManager.getOrCreate(projectKey)
				.getJiraIssueTextManager();

		if (ConfigPersistenceManager.isClassifierEnabled(projectKey)) {
			persistenceManager.deleteElementsInDescription(issueEvent.getIssue());
			// TODO This seems not to work for manual classified sentences. Check and fix
			classificationManagerForJiraIssueComments.classifyDescription((MutableIssue) issueEvent.getIssue());
		} else {
			persistenceManager.updateElementsOfDescriptionInDatabase(issueEvent.getIssue());
		}
		persistenceManager.createLinksForNonLinkedElements(issueEvent.getIssue());
	}

	private void replaceIconsWithTags() {
		String projectKey = issueEvent.getProject().getKey();
		if (!ConfigPersistenceManager.isIconParsing(projectKey)) {
			return;
		}
		MutableComment comment = (MutableComment) issueEvent.getComment();
		if (comment == null) {
			MutableIssue jiraIssue = (MutableIssue) issueEvent.getIssue();
			String description = jiraIssue.getDescription();
			description = replaceIconsWithTags(description);
			jiraIssue.setDescription(description);
			JiraIssuePersistenceManager.updateJiraIssue(jiraIssue, issueEvent.getUser());
		} else {
			String commentBody = comment.getBody();
			commentBody = replaceIconsWithTags(commentBody);
			comment.setBody(commentBody);
			ComponentAccessor.getCommentManager().update(comment, true);
		}
	}

	/**
	 * @param text
	 *            with icons, e.g. (!) for an issue.
	 * @return text in that all icons are replaced with Jira macro tags, e.g. (!)...
	 *         is replaced with {issue}...{issue}.
	 */
	public static String replaceIconsWithTags(String text) {
		// find all icons and split text by occurence of icons
		Pattern pattern = Pattern.compile("(.*)\\((.*)");
		Matcher matcher = pattern.matcher(text);
		String textWithoutIcons = "";
		boolean wasAnyIconReplaced = false;
		while (matcher.find()) {
			wasAnyIconReplaced = true;
			String sentence = text.substring(matcher.start(), matcher.end());
			for (KnowledgeType type : KnowledgeType.values()) {
				sentence = replaceIconsWithTags(sentence, type);
			}
			textWithoutIcons += sentence;
		}
		return wasAnyIconReplaced ? textWithoutIcons : text;
	}

	/**
	 * @param text
	 *            with icons, e.g. (!) for an issue.
	 * @param type
	 *            {@link KnowledgeType} for that the icons should be replaced by
	 *            macro tags, e.g. {@link KnowledgeType#ISSUE}.
	 * @return text in that all icons are replaced with Jira macro tags, e.g. (!)...
	 *         is replaced with {issue}...{issue}.
	 */
	public static String replaceIconsWithTags(String text, KnowledgeType type) {
		String icon = type.getIconString();
		if (icon.isBlank() || !text.contains(icon)) {
			return text;
		}
		String textWithoutIcons = text;
		textWithoutIcons = text.replaceFirst(icon.replace("(", "\\(").replace(")", "\\)"), "");
		return type.getTag() + textWithoutIcons.trim() + type.getTag();
	}

	@Override
	public void onProjectDeletion(ProjectDeletedEvent projectDeletedEvent) {
		projectKey = projectDeletedEvent.getProject().getKey();
		new JiraIssueTextPersistenceManager(projectKey).deleteElementsOfProject();
	}
}