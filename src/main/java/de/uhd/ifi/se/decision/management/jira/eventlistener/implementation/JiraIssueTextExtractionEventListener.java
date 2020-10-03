package de.uhd.ifi.se.decision.management.jira.eventlistener.implementation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.core.entity.GenericEntityException;
import org.ofbiz.core.entity.GenericValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.comments.MutableComment;
import com.atlassian.jira.util.collect.MapBuilder;

import de.uhd.ifi.se.decision.management.jira.classification.implementation.ClassificationManagerForJiraIssueComments;
import de.uhd.ifi.se.decision.management.jira.eventlistener.IssueEventListener;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.text.TextSplitter;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssuePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssueTextPersistenceManager;

/**
 * Triggers the extraction of decision knowledge elements and their integration
 * in the knowledge graph when the user changes either a comment or the
 * description of a Jira issue.
 */
public class JiraIssueTextExtractionEventListener implements IssueEventListener {

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

	@Override
	public void onIssueEvent(IssueEvent issueEvent) {
		this.issueEvent = issueEvent;
		this.projectKey = issueEvent.getProject().getKey();

		if (!ConfigPersistenceManager.isActivated(this.projectKey)) {
			return;
		}

		long eventTypeId = issueEvent.getEventTypeId();
		// @issue How to implement multiple checks of the event ID?
		// @decision Use ifs only!
		// @pro switch-case not trivial to implement,
		// because the switch can not be a long variable.
		// casting from long to int is dangerous.
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
		if (eventTypeId == EventType.ISSUE_UPDATED_ID || eventTypeId == EventType.ISSUE_CREATED_ID) {
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
			MutableIssue jiraIssue = (MutableIssue) issueEvent.getIssue();
			String description = jiraIssue.getDescription();
			description = TextSplitter.parseIconsToTags(description);
			jiraIssue.setDescription(description);
			JiraIssuePersistenceManager.updateJiraIssue(jiraIssue, issueEvent.getUser());
		} else {
			String commentBody = comment.getBody();
			commentBody = TextSplitter.parseIconsToTags(commentBody);
			comment.setBody(commentBody);
			ComponentAccessor.getCommentManager().update(comment, true);
		}
	}

	private void handleDeleteIssue() {
		JiraIssueTextPersistenceManager persistenceManager = KnowledgePersistenceManager.getOrCreate(projectKey)
				.getJiraIssueTextManager();
		persistenceManager.deleteInvalidElements(issueEvent.getUser());
		KnowledgeElement element = new KnowledgeElement(issueEvent.getIssue());
		KnowledgeGraph.getOrCreate(element.getProject().getProjectKey()).removeVertex(element);
		// JiraIssueTextPersistenceManager.createLinksForNonLinkedElementsForIssue(issueEvent.getIssue().getId());
	}

	private void handleDeleteComment() {
		JiraIssueTextPersistenceManager persistenceManager = KnowledgePersistenceManager.getOrCreate(projectKey)
				.getJiraIssueTextManager();
		persistenceManager.deleteInvalidElements(issueEvent.getUser());
		// JiraIssueTextPersistenceManager.createLinksForNonLinkedElementsForIssue(issueEvent.getIssue().getId());
	}

	private void handleNewComment() {
		parseIconsToTags();
		if (ConfigPersistenceManager.isClassifierEnabled(projectKey)) {
			this.classificationManagerForJiraIssueComments.classifyComment(this.issueEvent.getComment());
		} else {
			MutableComment comment = (MutableComment) issueEvent.getComment();
			JiraIssueTextPersistenceManager.insertPartsOfComment(comment);
		}
		// JiraIssueTextPersistenceManager.createLinksForNonLinkedElementsForIssue(issueEvent.getIssue().getId());

	}

	private void handleEditComment() {
		if (JiraIssueTextExtractionEventListener.editCommentLock) {
			// If locked, a REST service is currently manipulating the comment and should
			// not be handled by this event listener.
			LOGGER.debug("DecXtract event listener:\nEditing comment is still locked.");
			return;
		}

		parseIconsToTags();

		JiraIssueTextPersistenceManager persistenceManager = KnowledgePersistenceManager.getOrCreate(projectKey)
				.getJiraIssueTextManager();

		if (ConfigPersistenceManager.isClassifierEnabled(projectKey)) {
			persistenceManager.deleteElementsInComment(issueEvent.getComment());
			this.classificationManagerForJiraIssueComments.classifyComment(issueEvent.getComment());
		} else {
			MutableComment comment = (MutableComment) issueEvent.getComment();
			persistenceManager.updateComment(comment);
		}
		persistenceManager.createLinksForNonLinkedElements(issueEvent.getIssue());
	}

	private void handleUpdateDescription() {
		if (JiraIssueTextExtractionEventListener.editCommentLock) {
			// If locked, a REST service is currently manipulating the comment and should
			// not be handled by this event listener.
			LOGGER.debug("DecXtract event listener:\nEditing description is still locked.");
			return;
		}

		parseIconsToTags();

		JiraIssueTextPersistenceManager persistenceManager = KnowledgePersistenceManager.getOrCreate(projectKey)
				.getJiraIssueTextManager();

		if (ConfigPersistenceManager.isClassifierEnabled(projectKey)) {
			persistenceManager.deleteElementsInDescription(issueEvent.getIssue());
			classificationManagerForJiraIssueComments.classifyDescription((MutableIssue) issueEvent.getIssue());
		} else {
			persistenceManager.updateDescription(issueEvent.getIssue());
		}

		persistenceManager.createLinksForNonLinkedElements(issueEvent.getIssue());
	}

	public Map<String, String> getChangedString(IssueEvent issueEvent) {
		Map<String, String> changedString = new HashMap<String, String>();

		GenericValue changeLog = issueEvent.getChangeLog();
		Long changeId = changeLog.getLong("id");
		if (changeId == null) {
			return changedString;
		}

		try {
			List<GenericValue> changes = changeLog.internalDelegator.findByAnd("ChangeItem",
					MapBuilder.build("group", changeId));
			for (GenericValue changeItem : changes) {
				String oldValue = changeItem.getString("oldstring");
				String newValue = changeItem.getString("newstring");
				changedString.put(oldValue, newValue);
			}
		} catch (NullPointerException | GenericEntityException e) {
			LOGGER.error("Get changed string during a JIRA issue event failed. Message: " + e.getMessage());
		}

		return changedString;
	}
}