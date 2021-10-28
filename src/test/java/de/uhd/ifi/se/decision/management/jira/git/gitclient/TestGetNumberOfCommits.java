package de.uhd.ifi.se.decision.management.jira.git.gitclient;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.atlassian.jira.issue.Issue;

import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;

public class TestGetNumberOfCommits extends TestSetUpGit {

	@Test
	public void testJiraIssueKeyEmptyString() {
		assertEquals(0, gitClient.getNumberOfCommitsOnDefaultBranches((Issue) null));
	}

	@Test
	public void testJiraIssueKeyExisting() {
		assertEquals(1, gitClient.getNumberOfCommitsOnDefaultBranches(JiraIssues.getJiraIssueByKey("TEST-12")));
	}
}
