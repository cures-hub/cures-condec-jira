package de.uhd.ifi.se.decision.management.jira.extraction.gitclient;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestGetNumberOfCommits extends TestSetUpGit {

	@Test
	public void testJiraIssueKeyEmptyString() {
		assertEquals(0, gitClient.getNumberOfCommits(""));
	}

	@Test
	public void testJiraIssueKeyNull() {
		assertEquals(0, gitClient.getNumberOfCommits(null));
	}

	@Test
	public void testJiraIssueKeyExisting() {
		assertEquals(2, gitClient.getNumberOfCommits("TEST-12"));
	}
}
