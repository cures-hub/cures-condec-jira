package de.uhd.ifi.se.decision.management.jira.extraction.gitclient;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;

public class TestGetDefaultBranchCommits extends TestSetUpGit {

	@Test
	public void testDefaultBranchCommits() {
		List<RevCommit> allCommits = gitClient.getDefaultBranchCommits();
		assertEquals(7, allCommits.size());
	}

	@Test
	public void testDefaultBranchCommitsOfJiraIssue() {
		List<RevCommit> allCommits = gitClient.getDefaultBranchCommits(mockJiraIssueForGitTests);
		assertEquals(2, allCommits.size());
	}

	@Test
	public void testDefaultBranchCommitsOfJiraIssueNull() {
		List<RevCommit> allCommits = gitClient.getDefaultBranchCommits(null);
		assertEquals(0, allCommits.size());
	}
}
