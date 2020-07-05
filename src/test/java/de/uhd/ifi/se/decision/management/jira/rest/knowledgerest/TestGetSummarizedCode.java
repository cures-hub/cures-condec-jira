package de.uhd.ifi.se.decision.management.jira.rest.knowledgerest;

import javax.ws.rs.core.Response.Status;

import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.rest.KnowledgeRest;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestGetSummarizedCode extends TestSetUpGit {

	private KnowledgeRest knowledgeRest;

	@Override
	@Before
	public void setUp() {
		knowledgeRest = new KnowledgeRest();
		super.setUp();
	}

	@Test
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