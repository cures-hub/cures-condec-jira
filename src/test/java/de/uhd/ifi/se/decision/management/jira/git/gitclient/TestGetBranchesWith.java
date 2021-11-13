package de.uhd.ifi.se.decision.management.jira.git.gitclient;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestGetBranchesWith extends TestSetUpGit {

	@Test
	public void testDiffViewerConstructorBranches() {
		assertEquals(1, gitClient.getBranchesWithKnowledge("TEST").size());
	}

}
