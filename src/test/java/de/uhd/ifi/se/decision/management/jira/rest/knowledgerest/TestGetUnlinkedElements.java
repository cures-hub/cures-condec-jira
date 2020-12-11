package de.uhd.ifi.se.decision.management.jira.rest.knowledgerest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.rest.KnowledgeRest;

public class TestGetUnlinkedElements extends TestSetUp {

	private KnowledgeRest knowledgeRest;

	@Before
	public void setUp() {
		init();
		knowledgeRest = new KnowledgeRest();
	}

	@Test
	public void testElementIdFilledProjectExistentDocumentationLocationJiraIssue() {
		assertEquals(Status.OK.getStatusCode(), knowledgeRest.getUnlinkedElements(7, "TEST", "i").getStatus());
	}

	@Test
	public void testElementIdZeroProjectExistentDocumentationLocationEmpty() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), knowledgeRest.getUnlinkedElements(0, "TEST", "").getStatus());
	}

	@Test
	public void testElementIdZeroProjectKeyNullDocumentationLocationEmpty() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), knowledgeRest.getUnlinkedElements(0, null, "").getStatus());
	}

	@Test
	public void testElementIdFilledProjectKeyNullDocumentationLocationEmpty() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), knowledgeRest.getUnlinkedElements(7, null, "").getStatus());
	}

	@Test
	public void testElementIdFilledProjectExistentDocumentationLocationEmpty() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), knowledgeRest.getUnlinkedElements(7, "TEST", "").getStatus());
	}
}
