package de.uhd.ifi.se.decision.management.jira.extraction.gitclient;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.atlassian.jira.issue.Issue;

public class TestGetNumberOfCommits extends TestSetUpGit {

	@Test
	public void testJiraIssueKeyEmptyString() {
		assertEquals(0, gitClient.getNumberOfCommitsOnDefaultBranches((Issue) null));
	}

	@Test
	public void testJiraIssueKeyExisting() {
		assertEquals(1, gitClient.getNumberOfCommitsOnDefaultBranches(mockJiraIssueForGitTests));
	}
}
