package de.uhd.ifi.se.decision.management.jira.rest.gitrest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.rest.GitRest;

public class TestGetSummarizedCode extends TestSetUpGit {

	private GitRest knowledgeRest;

	@Override
	@Before
	public void setUp() {
		knowledgeRest = new GitRest();
		super.setUp();
	}

	@Test
	@Ignore
	public void testElementIdFilledProjectExistentDocumentationLocationJiraIssue() {
		assertEquals(Status.OK.getStatusCode(), knowledgeRest.getSummarizedCode(14, "TEST", "i", 0).getStatus());
	}

	@Test
	public void testElementIdNegativeProjectExistentDocumentationLocationJiraIssue() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				knowledgeRest.getSummarizedCode(-1, "TEST", "i", 0).getStatus());
	}

	@Test
	public void testElementIdFilledProjectNullDocumentationLocationJiraIssue() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), knowledgeRest.getSummarizedCode(12, null, "i", 0).getStatus());
	}
}