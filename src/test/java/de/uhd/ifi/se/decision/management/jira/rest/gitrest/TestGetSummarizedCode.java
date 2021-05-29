package de.uhd.ifi.se.decision.management.jira.rest.gitrest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.extraction.config.GitConfiguration;
import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.rest.GitRest;

public class TestGetSummarizedCode extends TestSetUpGit {

	private GitRest gitRest;

	@Override
	@Before
	public void setUp() {
		gitRest = new GitRest();
		super.setUp();
	}

	@Test
	public void testElementIdFilledProjectExistentDocumentationLocationJiraIssue() {
		GitConfiguration gitConfig = ConfigPersistenceManager.getGitConfiguration("TEST");
		gitConfig.setActivated(true);
		ConfigPersistenceManager.saveGitConfiguration("TEST", gitConfig);
		assertEquals(Status.OK.getStatusCode(), gitRest.getSummarizedCode(14, "TEST", "i", 0).getStatus());
	}

	@Test
	public void testElementIdNegativeProjectExistentDocumentationLocationJiraIssue() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), gitRest.getSummarizedCode(-1, "TEST", "i", 0).getStatus());
	}

	@Test
	public void testElementIdFilledProjectNullDocumentationLocationJiraIssue() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), gitRest.getSummarizedCode(12, null, "i", 0).getStatus());
	}
}