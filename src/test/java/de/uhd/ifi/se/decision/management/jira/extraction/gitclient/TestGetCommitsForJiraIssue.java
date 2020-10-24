package de.uhd.ifi.se.decision.management.jira.extraction.gitclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;

public class TestGetCommitsForJiraIssue extends TestSetUpGit {

	@Test
	public void testJiraIssueNull() {
		List<RevCommit> commits = gitClient.getCommits(null);
		assertEquals(0, commits.size());
	}

	@Test
	public void testJiraIssue() {
		// TODO Pass branch to getCommits method or iterate over all branches if no
		// branch is given
		List<RevCommit> commits = gitClient.getCommits(mockJiraIssueForGitTests);
		System.out.println(commits);
		for (RevCommit commit : commits) {
			System.out.println(commit.getFullMessage());
		}
		assertEquals(2, commits.size()); // should be 3: two commits are on the default branch, one commit is on the
											// feature branch
		assertTrue(commits.get(0).getShortMessage().startsWith("TEST-12: Develop great software"));
	}

	@Test
	public void testGetJiraIssueKeyFromEmptyMessage() {
		String jiraIssueKey = GitClient.getJiraIssueKey("");
		assertEquals("", jiraIssueKey);
	}

	@Test
	public void testGetJiraIssueKeyFromValidMessage() {
		String jiraIssueKey = GitClient.getJiraIssueKey("Test-12: This is a very advanced commit.");
		assertEquals("TEST-12", jiraIssueKey);
	}

	@Test
	public void testGitNull() {
		GitClient gitClient = new GitClient();
		List<RevCommit> commits = gitClient.getCommits(mockJiraIssueForGitTests);
		assertEquals(0, commits.size());
	}
}
