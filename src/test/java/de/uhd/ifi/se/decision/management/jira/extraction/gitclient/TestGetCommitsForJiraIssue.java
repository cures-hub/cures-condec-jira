package de.uhd.ifi.se.decision.management.jira.extraction.gitclient;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.GitClientImpl;

public class TestGetCommitsForJiraIssue extends TestSetUpGit {

	@Test
	public void testJiraIssueKeyEmptyString() {
		List<RevCommit> commits = gitClient.getCommits("");
		assertEquals(0, commits.size());
	}

	@Test
	public void testJiraIssueKeyNull() {
		List<RevCommit> commits = gitClient.getCommits(null);
		assertEquals(0, commits.size());
	}

	@Test
	public void testJiraIssueKeyExisting() {
		List<RevCommit> commits = gitClient.getCommits("TEST-12");
		assertEquals(2, commits.size());
		assertEquals("TEST-12: Develop great software", commits.get(1).getShortMessage());
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
		GitClient gitClient = new GitClientImpl();
		List<RevCommit> commits = gitClient.getCommits("");
		assertEquals(0, commits.size());
	}
}
