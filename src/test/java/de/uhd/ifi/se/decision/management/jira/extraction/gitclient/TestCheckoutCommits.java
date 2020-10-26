package de.uhd.ifi.se.decision.management.jira.extraction.gitclient;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;

public class TestCheckoutCommits extends TestSetUpGit {

	@Test
	public void testCommitCheckout() {
		List<RevCommit> allCommits = gitClient.getAllCommits();
		assertTrue(2 < allCommits.size());
		assertTrue(gitClient.getGitClientsForSingleRepo(GIT_URI).checkoutCommit(allCommits.get(allCommits.size() - 2)));
		assertTrue(gitClient.getGitClientsForSingleRepo(GIT_URI).checkoutCommit(allCommits.get(0)));
	}
}
