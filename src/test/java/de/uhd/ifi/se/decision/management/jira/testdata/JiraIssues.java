package de.uhd.ifi.se.decision.management.jira.testdata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.issuetype.MockIssueType;
import com.atlassian.jira.mock.issue.MockIssue;
import com.atlassian.jira.project.Project;

import de.uhd.ifi.se.decision.management.jira.extraction.TestTextSplitter;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueTextPersistenceManager;

public class JiraIssues {

	private static List<MutableIssue> jiraIssues;

	public static List<MutableIssue> getTestJiraIssues() {
		if (jiraIssues == null || jiraIssues.isEmpty()) {
			jiraIssues = createJiraIssues(JiraProjects.TEST.createJiraProject(1));
		}
		return jiraIssues;
	}

	public static List<MutableIssue> createJiraIssues(Project project) {
		List<MutableIssue> jiraIssues = new ArrayList<MutableIssue>();

		if (project == null) {
			return jiraIssues;
		}

		List<IssueType> jiraIssueTypes = JiraIssueTypes.getTestTypes();

		List<KnowledgeType> types = Arrays.asList(KnowledgeType.values());
		MutableIssue issue = createJiraIssue(30, "TEST-" + 30, jiraIssueTypes.get(13), project);
		jiraIssues.add(issue);

		for (int i = 2; i < jiraIssueTypes.size() + 2; i++) {
			issue = createJiraIssue(i, "TEST-" + i, jiraIssueTypes.get(i - 2), project);
			if (i > types.size() - 4) {
				issue.setParentId((long) 3);
			}
			jiraIssues.add(issue);
		}
		IssueType issueType = new MockIssueType(50, "Class");
		issue = createJiraIssue(50, "TEST-" + 50, issueType, project);
		issue.setParentId((long) 3);
		jiraIssues.add(issue);
		return jiraIssues;
	}

	private static MutableIssue createJiraIssue(int id, String key, IssueType issueType, Project project) {
		MutableIssue issue = new MockIssue(id, key);
		((MockIssue) issue).setProjectId(project.getId());
		issue.setProjectObject(project);
		issue.setIssueType(issueType);
		issue.setSummary("Test");
		issue.setDescription("Test");
		return issue;
	}

	public static Issue addComment(Issue issue) {
		List<PartOfJiraIssueText> comment = TestTextSplitter.getSentencesForCommentText("{issue} testobject {issue}");
		PartOfJiraIssueText sentence = comment.get(0);
		sentence.setJiraIssueId(issue.getId());
		JiraIssueTextPersistenceManager.insertDecisionKnowledgeElement(sentence,
				JiraUsers.SYS_ADMIN.getApplicationUser());

		return sentence.getJiraIssue();
	}

}
