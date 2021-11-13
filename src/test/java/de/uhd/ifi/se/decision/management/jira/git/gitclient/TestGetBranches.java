package de.uhd.ifi.se.decision.management.jira.git.gitclient;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestGetBranches extends TestSetUpGit {

	@Test
	public void testDiffViewerConstructorBranches() {
		assertEquals(1, gitClient.getBranches("TEST").size());
	}

}
