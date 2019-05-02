package de.uhd.ifi.se.decision.management.jira.extraction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.core.entity.GenericEntityException;
import org.ofbiz.core.entity.GenericValue;
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
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.comments.MutableComment;
import com.atlassian.jira.util.collect.MapBuilder;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;

import de.uhd.ifi.se.decision.management.jira.model.text.TextSplitter;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssuePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueTextPersistenceManager;

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
		JiraIssueTextPersistenceManager.cleanSentenceDatabase(projectKey);
		JiraIssueTextPersistenceManager.createLinksForNonLinkedElementsForIssue(issueEvent.getIssue().getId());
	}

	private void handleDeleteComment() {
		JiraIssueTextPersistenceManager.cleanSentenceDatabase(this.projectKey);
		JiraIssueTextPersistenceManager.createLinksForNonLinkedElementsForIssue(issueEvent.getIssue().getId());
	}

	private void handleNewComment() {
		parseIconsToTags();
		if (ConfigPersistenceManager.isUseClassiferForIssueComments(this.projectKey)) {
			new ClassificationManagerForJiraIssueComments().classifyAllCommentsOfJiraIssue(this.issueEvent.getIssue());
		} else {
			MutableComment comment = (MutableComment) issueEvent.getComment();
			JiraIssueTextPersistenceManager.getPartsOfComment(comment);
		}
		JiraIssueTextPersistenceManager.createLinksForNonLinkedElementsForIssue(issueEvent.getIssue().getId());
	}

	private void handleEditComment() {
		if (DecXtractEventListener.editCommentLock) {
			// If locked, a REST service is currently manipulating the comment and should
			// not be handled by this event listener.
			LOGGER.debug("DecXtract event listener:\nEditing comment is still locked.");
			return;
		}

		parseIconsToTags();

		if (ConfigPersistenceManager.isUseClassiferForIssueComments(this.projectKey)) {
			JiraIssueTextPersistenceManager.deletePartsOfComment(issueEvent.getComment());
			new ClassificationManagerForJiraIssueComments().classifyAllCommentsOfJiraIssue(this.issueEvent.getIssue());
		} else {
			MutableComment comment = (MutableComment) issueEvent.getComment();
			JiraIssueTextPersistenceManager.updateComment(comment);
		}
		JiraIssueTextPersistenceManager.createLinksForNonLinkedElementsForIssue(issueEvent.getIssue().getId());
	}

	private void handleUpdateDescription() {
		if (DecXtractEventListener.editCommentLock) {
			// If locked, a REST service is currently manipulating the comment and should
			// not be handled by this event listener.
			LOGGER.debug("DecXtract event listener:\nEditing description is still locked.");
			return;
		}

		parseIconsToTags();

		if (ConfigPersistenceManager.isUseClassiferForIssueComments(this.projectKey)) {
			JiraIssueTextPersistenceManager.deletePartsOfDescription(issueEvent.getIssue());
			new ClassificationManagerForJiraIssueComments().classifyAllCommentsOfJiraIssue(this.issueEvent.getIssue());
		} else {
			JiraIssueTextPersistenceManager.updateDescription(issueEvent.getIssue());
		}
		JiraIssueTextPersistenceManager.createLinksForNonLinkedElementsForIssue(issueEvent.getIssue().getId());
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
			e.printStackTrace();
		}

		return changedString;
	}
}
