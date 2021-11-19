package de.uhd.ifi.se.decision.management.jira.git.gitclient;

import static org.junit.Assert.assertEquals;

import org.eclipse.jgit.lib.Ref;
import org.junit.Test;

public class TestGetRepoUriFromBranch extends TestSetUpGit {

	@Test
	public void testBranchExisting() {
		Ref featureBranch = gitClient.getDiff("TEST-4.feature.branch").getRefs().get(0);
		assertEquals(GIT_URI, gitClient.getRepoUriFromBranch(featureBranch));
	}

	@Test
	public void testBranchNull() {
		assertEquals("", gitClient.getRepoUriFromBranch(null));
	}
}
