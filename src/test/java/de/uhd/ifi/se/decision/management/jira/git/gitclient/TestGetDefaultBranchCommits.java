package de.uhd.ifi.se.decision.management.jira.git.gitclient;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;

public class TestGetDefaultBranchCommits extends TestSetUpGit {

	@Test
	public void testAllDefaultBranchCommits() {
		List<RevCommit> allCommits = gitClient.getDiffForDefaultBranches().getCommits();
		// TODO Decide whether to include commits
		// assertEquals(6, allCommits.size());
		assertEquals(0, allCommits.size());
	}

	@Test
	public void testDefaultBranchCommitsOfJiraIssue() {
		List<RevCommit> allCommits = gitClient.getDiffForJiraIssueOnDefaultBranches(mockJiraIssueForGitTests)
				.getCommits();
		assertEquals(1, allCommits.size());
	}
}
