package de.uhd.ifi.se.decision.management.jira.extraction.gitclient;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;

public class TestGetCommits extends TestSetUpGit {

	@Test
	public void testRepositoryExisting() {
		List<RevCommit> allCommits = gitClient.getAllCommits();
		int expectedOnDefaultBranch = 8;
		int expectedOnFeatureBranch = 22; /* all = unique to the branch + parent branch's commits */
		int expectedAllCommitsNumber = expectedOnDefaultBranch + expectedOnFeatureBranch;
		assertEquals(expectedAllCommitsNumber, allCommits.size()); // expects 30
	}

	@Test
	public void testGetCommitsFromDefaultBranch() {
		List<RevCommit> allCommits = gitClient.getGitClientsForSingleRepos().get(0).getCommitsFromDefaultBranch();
		int expectedOnDefaultBranch = 7;
		assertEquals(expectedOnDefaultBranch, allCommits.size());
	}
}
