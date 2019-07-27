package de.uhd.ifi.se.decision.management.jira.testdata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.issuetype.MockIssueType;
import com.atlassian.jira.mock.issue.MockIssue;
import com.atlassian.jira.project.Project;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class JiraIssues {

	public static List<MutableIssue> createJiraIssues(Project project) {
		List<MutableIssue> jiraIssues = new ArrayList<MutableIssue>();

		if (project == null) {
			return jiraIssues;
		}
		
		List<IssueType> jiraIssueTypes = JiraIssueTypes.getTestJiraIssueTypes();

		List<KnowledgeType> types = Arrays.asList(KnowledgeType.values());
		MockIssue issue = addJiraIssue(30, "TEST-" + 30, jiraIssueTypes.get(13), project);
		jiraIssues.add(issue);

		for (int i = 2; i < jiraIssueTypes.size() + 2; i++) {
			issue = addJiraIssue(i, "TEST-" + i, jiraIssueTypes.get(i - 2), project);
			if (i > types.size() - 4) {
				issue.setParentId((long) 3);
			}
			jiraIssues.add(issue);
		}
		IssueType issueType = new MockIssueType(50, "Class");
		issue = addJiraIssue(50, "TEST-" + 50, issueType, project);
		issue.setParentId((long) 3);
		jiraIssues.add(issue);
		return jiraIssues;
	}

	public static MockIssue addJiraIssue(int id, String key, IssueType issueType, Project project) {
		MutableIssue issue = new MockIssue(id, key);
		((MockIssue) issue).setProjectId(project.getId());
		issue.setProjectObject(project);
		issue.setIssueType(issueType);
		issue.setSummary("Test");
		issue.setDescription("Test");
		return (MockIssue) issue;
	}

}
