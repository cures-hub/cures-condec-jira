package de.uhd.ifi.se.decision.management.jira.extraction.git;

import java.io.IOException;
import java.util.Map;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.EditList;
import org.json.JSONException;
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
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.history.ChangeItemBean;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;

import de.uhd.ifi.se.decision.management.jira.extraction.CommentSplitterImpl;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueCommentPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.view.macros.AbstractKnowledgeClassificationMacro;

/**
 * Triggers the webhook when JIRA issues are created, updated, or deleted or
 * when links between JIRA issues are created or deleted
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
	public void onIssueEvent(IssueEvent issueEvent)
			throws IOException, JSONException, GitAPIException, InterruptedException {
		String projectKey = issueEvent.getProject().getKey();
		String issueId = issueEvent.getIssue().getKey();
		Long eventTypeId = issueEvent.getEventTypeId();
		if (ConfigPersistenceManager.isKnowledgeExtractedFromGit(projectKey)
				&& eventTypeId.equals(EventType.ISSUE_GENERICEVENT_ID) && isClosing(issueEvent)) {
			MutableIssue issue = ComponentAccessor.getIssueManager().getIssueObject(issueId);
			String text = "";
			try {
				Map<DiffEntry, EditList> diff = GitDiffExtraction.getGitDiff(projectKey, issueId);
				text = TaskCodeSummarizer.summarizer(diff, projectKey, false);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (GitAPIException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (!text.isEmpty()) {
				text = text.length() > 3500 ? text.substring(0, 3500) + "..." : text;
				String tag = AbstractKnowledgeClassificationMacro.getTag(KnowledgeType.CODESUMMARIZATION);
				text = tag + text + tag;

				Comment comment = ComponentAccessor.getCommentManager().create(issue,
						ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser(), text, false);
				// createSentence(comment, text);
				new CommentSplitterImpl().getSentences(comment);
				JiraIssueCommentPersistenceManager
						.createLinksForNonLinkedElementsForIssue(issueEvent.getIssue().getId());
			}

		}
	}

	private boolean isClosing(IssueEvent issueEvent) {
		GenericValue changeLog = issueEvent.getChangeLog();

		Long changeId = changeLog.getLong("id");
		if (changeId != null) {
			ChangeHistory change = changeManager.getChangeHistoryById(changeId);
			System.out.println("Changes: " + change.getChangeItemBeans());
			for (ChangeItemBean bean : change.getChangeItemBeans()) {
				if (bean.getToString().equalsIgnoreCase("DONE")) {
					return true;
				}
			}
		}
		return false;
	}
}