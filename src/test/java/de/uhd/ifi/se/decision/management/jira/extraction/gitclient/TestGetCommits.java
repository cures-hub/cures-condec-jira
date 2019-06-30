package de.uhd.ifi.se.decision.management.jira.extraction.gitclient;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;

public class TestGetCommits extends TestSetUpGit {

	@Test
	public void testRepositoryExisting() {
		List<RevCommit> allCommits = gitClient.getCommits();
		int expectedOnDefaultBranch = 3;
		int expectedOnFeatureBranch = 6; /* all = unique to the branch + parent branch's commits*/
		int expectedAllCommitsNumber = expectedOnDefaultBranch + expectedOnFeatureBranch;
		assertEquals(expectedAllCommitsNumber, allCommits.size());
	}
	
	@Test
	public void testMasterBranchExists() {
		Git git = gitClient.getGit();
		String currentBranch = null;
		try {
			currentBranch = git.getRepository().getBranch();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		assertEquals("master", currentBranch);
	}
}
