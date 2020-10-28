package de.uhd.ifi.se.decision.management.jira.extraction.gitclient;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class TestGetBranches extends TestSetUpGit {

	@Override
	@Before
	public void setUp() {
		super.setUp();
	}

	@Test
	public void testDefaultBranch() {
		assertEquals("refs/remotes/origin/master", gitClient.getBranches("master").get(0).getName());
	}

	@Test
	public void testFeatureBranch() {
		assertEquals("refs/remotes/origin/TEST-4.transcriberBranch",
				gitClient.getBranches("transcriberBranch").get(0).getName());
	}

	@Test
	public void testJiraIssueKey() {
		assertEquals("refs/remotes/origin/TEST-4.transcriberBranch", gitClient.getBranches("TEST-4").get(0).getName());
	}

}
