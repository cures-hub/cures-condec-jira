package de.uhd.ifi.se.decision.management.jira.extraction.gitclient;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.eclipse.jgit.lib.Ref;
import org.junit.Test;

public class TestGetRepoUriFromBranch extends TestSetUpGit {

	@Test
	public void testBranchExisting() {
		Ref featureBranch = gitClient.getBranches("TEST-4.feature.branch").get(0);
		assertEquals(GIT_URI, gitClient.getRepoUriFromBranch(featureBranch));
	}

	@Test
	public void testBranchNull() {
		assertEquals("", gitClient.getRepoUriFromBranch(null));
	}
}
