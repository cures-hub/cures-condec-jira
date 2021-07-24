package de.uhd.ifi.se.decision.management.jira.rest.knowledgerest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.rest.KnowledgeRest;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestGetKnowledgeElement extends TestSetUp {
	private KnowledgeRest knowledgeRest;

	@Before
	public void setUp() {
		init();
		knowledgeRest = new KnowledgeRest();
	}

	@Test
	public void testElementIdFilledProjectExistentDocumentationLocationNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				knowledgeRest.getKnowledgeElement(3, "TEST", null).getStatus());
	}

	@Test
	public void testElementIdZeroProjectKeyNullDocumentationLocationNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), knowledgeRest.getKnowledgeElement(0, null, null).getStatus());
	}

	@Test
	public void testElementIdZeroProjectExistentDocumentationLocationNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				knowledgeRest.getKnowledgeElement(0, "TEST", null).getStatus());
	}

	@Test
	public void testElementIdFilledProjectKeyNullDocumentationLocationNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), knowledgeRest.getKnowledgeElement(7, null, null).getStatus());
	}

	@Test
	public void testElementNotExistentProjectExistentDocumentationLocationJiraIssue() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				knowledgeRest.getKnowledgeElement(300, "TEST", "i").getStatus());
	}

	@Test
	public void testElementExistentProjectExistentDocumentationLocationJiraIssueComment() {
		assertEquals(Status.OK.getStatusCode(),
				knowledgeRest
						.getKnowledgeElement(KnowledgeElements.getTestKnowledgeElement().getId(),
								KnowledgeElements.getTestKnowledgeElement().getProject().toString(),
								KnowledgeElements.getTestKnowledgeElement().getDocumentationLocationAsString())
						.getStatus());
	}

	@Test
	public void testElementNotExistentProjectExistentDocumentationLocationJiraIssueComment() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), knowledgeRest.getKnowledgeElement(7, "TEST", "s").getStatus());
	}

	@Test
	public void testElementNotExistentProjectExistentDocumentationLocationCodeComment() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				knowledgeRest.getKnowledgeElement(-7, "TEST", "c").getStatus());
	}
}
