package de.uhd.ifi.se.decision.management.jira.eventlistener.implementation;

import org.ofbiz.core.entity.GenericValue;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.changehistory.ChangeHistory;
import com.atlassian.jira.issue.changehistory.ChangeHistoryManager;
import com.atlassian.jira.issue.history.ChangeItemBean;

import de.uhd.ifi.se.decision.management.jira.eventlistener.IssueEventListener;
import de.uhd.ifi.se.decision.management.jira.extraction.CodeSummarizer;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;

/**
 * Triggers the code summarization when Jira issues are closed. Then, the
 * summary is written into a new comment of the Jira issue.
 */
public class SummarizationEventListener implements IssueEventListener {

	private final ChangeHistoryManager changeManager = ComponentAccessor.getChangeHistoryManager();

	public void onIssueEvent(IssueEvent issueEvent) {
		String projectKey = issueEvent.getProject().getKey();
		String jiraIssueKey = issueEvent.getIssue().getKey();
		long eventTypeId = issueEvent.getEventTypeId();

		if (!ConfigPersistenceManager.getGitConfiguration(projectKey).isActivated()
				|| eventTypeId != EventType.ISSUE_GENERICEVENT_ID || !isClosing(issueEvent)) {
			return;
		}

		MutableIssue issue = ComponentAccessor.getIssueManager().getIssueObject(jiraIssueKey);
		CodeSummarizer summarizer = new CodeSummarizer(projectKey);
		summarizer.setFormatForComments(true);
		String summary = summarizer.createSummary(issueEvent.getIssue(), 0);

		if (summary.isEmpty()) {
			return;
		}
		summary = summary.length() > 3500 ? summary.substring(0, 3500) + "..." : summary;
		String tag = "{codesummarization}";
		String commentBody = tag + summary + tag;

		ComponentAccessor.getCommentManager().create(issue,
				ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser(), commentBody, false);
	}

	private boolean isClosing(IssueEvent issueEvent) {
		GenericValue changeLog = issueEvent.getChangeLog();

		Long changeId = changeLog.getLong("id");
		if (changeId == null) {
			return false;
		}
		ChangeHistory changeHistory = changeManager.getChangeHistoryById(changeId);
		for (ChangeItemBean changeItemBean : changeHistory.getChangeItemBeans()) {
			if (changeItemBean.getToString().equalsIgnoreCase("DONE")) {
				return true;
			}
		}
		return false;
	}
}