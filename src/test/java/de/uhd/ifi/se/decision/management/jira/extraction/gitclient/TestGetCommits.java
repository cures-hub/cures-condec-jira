package de.uhd.ifi.se.decision.management.jira.extraction.gitclient;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;

public class TestGetCommits extends TestSetUpGit {

	@Test
	public void testGetCommitsFromDefaultBranch() {
		List<RevCommit> allCommits = gitClient.getGitClientsForSingleRepos().get(0).getDefaultBranchCommits();
		int expectedOnDefaultBranch = 7;
		assertEquals(expectedOnDefaultBranch, allCommits.size());
	}
}
