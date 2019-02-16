package de.uhd.ifi.se.decision.management.jira.extraction.git;

import java.io.IOException;
import java.util.Map;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.EditList;
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

import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;

/**
 * Triggers the code summarization when JIRA issues are closed. Then, the
 * summary is written into a new comment of the JIRA issue.
 */
@Component
public class TaskCodeSummarizationEventListener implements InitializingBean, DisposableBean {

	private final ChangeHistoryManager changeManager = ComponentAccessor.getChangeHistoryManager();

	@JiraImport
	private final EventPublisher eventPublisher;

	@Autowired
	public TaskCodeSummarizationEventListener(EventPublisher eventPublisher) {
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
		String projectKey = issueEvent.getProject().getKey();
		String jiraIssueKey = issueEvent.getIssue().getKey();
		long eventTypeId = issueEvent.getEventTypeId();

		if (!ConfigPersistenceManager.isKnowledgeExtractedFromGit(projectKey)
				|| eventTypeId != EventType.ISSUE_GENERICEVENT_ID || !isClosing(issueEvent)) {
			return;
		}

		MutableIssue issue = ComponentAccessor.getIssueManager().getIssueObject(jiraIssueKey);
		String summarization = "";
		try {
			GitClient gitClient = new GitClient(projectKey);
			Map<DiffEntry, EditList> diff = gitClient.getCodeDiff(projectKey, jiraIssueKey);
			summarization = TaskCodeSummarizer.summarizer(diff, projectKey, false);
		} catch (IOException | GitAPIException e) {
			e.printStackTrace();
		}

		if (summarization.isEmpty()) {
			return;
		}
		summarization = summarization.length() > 3500 ? summarization.substring(0, 3500) + "..." : summarization;
		String tag = "{codesummarization}";
		String commentBody = tag + summarization + tag;

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