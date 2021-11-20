package de.uhd.ifi.se.decision.management.jira.git.gitclient;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;

import com.atlassian.jira.issue.Issue;

import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;

public class TestGetDiffForJiraIssueOnDefaultBranches extends TestSetUpGit {

	@Test
	public void testJiraIssueWithCommits() {
		Issue issue = JiraIssues.getJiraIssueByKey("TEST-14");
		List<RevCommit> commits = gitClient.getDiffForJiraIssueOnDefaultBranches(issue).getCommits();
		assertEquals(3, commits.size());
	}

	@Test
	public void testJiraIssueNull() {
		List<RevCommit> commits = gitClient.getDiffForJiraIssueOnDefaultBranches(null).getCommits();
		assertEquals(0, commits.size());
	}
}
