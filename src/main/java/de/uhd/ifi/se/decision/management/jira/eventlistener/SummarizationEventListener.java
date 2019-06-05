package de.uhd.ifi.se.decision.management.jira.eventlistener;

import org.ofbiz.core.entity.GenericValue;
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
import com.atlassian.jira.issue.changehistory.ChangeHistory;
import com.atlassian.jira.issue.changehistory.ChangeHistoryManager;
import com.atlassian.jira.issue.history.ChangeItemBean;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;

import de.uhd.ifi.se.decision.management.jira.extraction.impl.CodeSummarizerImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;

/**
 * Triggers the code summarization when JIRA issues are closed. Then, the
 * summary is written into a new comment of the JIRA issue.
 */
public class SummarizationEventListener{

	private final ChangeHistoryManager changeManager = ComponentAccessor.getChangeHistoryManager();


	public void onIssueEvent(IssueEvent issueEvent) {
		String projectKey = issueEvent.getProject().getKey();
		String jiraIssueKey = issueEvent.getIssue().getKey();
		long eventTypeId = issueEvent.getEventTypeId();

		if (!ConfigPersistenceManager.isKnowledgeExtractedFromGit(projectKey)
				|| eventTypeId != EventType.ISSUE_GENERICEVENT_ID || !isClosing(issueEvent)) {
			return;
		}

		MutableIssue issue = ComponentAccessor.getIssueManager().getIssueObject(jiraIssueKey);
		String summary = new CodeSummarizerImpl(projectKey).createSummary(issueEvent.getIssue());

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