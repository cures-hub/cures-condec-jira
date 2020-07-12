package de.uhd.ifi.se.decision.management.jira.extraction.gitclient;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class TestCheckoutCommits extends TestSetUpGit {

	@Test
	public void testCommitCheckout() {
		List<RevCommit> allCommits = gitClient.getCommits(GIT_URI);
		assertTrue(2 < allCommits.size());
		assertTrue(gitClient.checkoutCommit(allCommits.get(allCommits.size() - 2), GIT_URI));
		assertTrue(gitClient.checkoutCommit(allCommits.get(0), GIT_URI));
	}
}
