package de.uhd.ifi.se.decision.management.jira.mocks;

import java.util.Collection;
import java.util.stream.Collectors;

import org.ofbiz.core.entity.GenericEntityException;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.MutableIssue;

import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;

/**
 * This class mocks the JIRA issue manager. It implements methods that are not
 * implemented in the MockIssueManager of the jira.mock package.
 */
public class MockIssueManager extends com.atlassian.jira.mock.MockIssueManager {

	@Override
	public Collection<Long> getIssueIdsForProject(Long id) throws GenericEntityException {
		return JiraIssues.getTestJiraIssues().stream().map(Issue::getId).collect(Collectors.toList());
	}

	@Override
	public MutableIssue getIssueObject(String key) {
		return getIssueByCurrentKey(key);
	}

	@Override
	public MutableIssue getIssueByCurrentKey(String key) {
		Issue mutableIssue = JiraIssues.getTestJiraIssues().stream().filter(jiraIssue -> key.equals(jiraIssue.getKey()))
				.findFirst().orElse(null);
		return (MutableIssue) mutableIssue;
	}
}
