package de.uhd.ifi.se.decision.management.jira.rest.knowledgerest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.rest.KnowledgeRest;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response.Status;

import static org.junit.Assert.assertEquals;

public class TestGetDecisionKnowledgeElement extends TestSetUp {
	private KnowledgeRest knowledgeRest;

	@Before
	public void setUp() {
		init();
		knowledgeRest = new KnowledgeRest();
	}

	@Test
	public void testElementIdFilledProjectExistentDocumentationLocationNull() {
		assertEquals(Status.NOT_FOUND.getStatusCode(),
			knowledgeRest.getDecisionKnowledgeElement(3, "TEST", null).getStatus());
	}

	@Test
	public void testElementIdZeroProjectKeyNullDocumentationLocationNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
			knowledgeRest.getDecisionKnowledgeElement(0, null, null).getStatus());
	}

	@Test
	public void testElementIdZeroProjectExistentDocumentationLocationNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
			knowledgeRest.getDecisionKnowledgeElement(0, "TEST", null).getStatus());
	}

	@Test
	public void testElementIdFilledProjectKeyNullDocumentationLocationNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
			knowledgeRest.getDecisionKnowledgeElement(7, null, null).getStatus());
	}

	@Test
	public void testElementNotExistentProjectExistentDocumentationLocationJiraIssue() {
		assertEquals(Status.NOT_FOUND.getStatusCode(),
			knowledgeRest.getDecisionKnowledgeElement(300, "TEST", "i").getStatus());
	}

	@Test
	public void testElementExistentProjectExistentDocumentationLocationJiraIssueComment() {
		assertEquals(Status.OK.getStatusCode(),
			knowledgeRest.getDecisionKnowledgeElement(
				KnowledgeElements.getTestKnowledgeElement().getId(),
				KnowledgeElements.getTestKnowledgeElement().getProject().toString(),
				KnowledgeElements.getTestKnowledgeElement().getDocumentationLocationAsString()
			).getStatus());
	}

	@Test
	public void testElementNotExistentProjectExistentDocumentationLocationJiraIssueComment() {
		assertEquals(Status.NOT_FOUND.getStatusCode(),
			knowledgeRest.getDecisionKnowledgeElement(7, "TEST", "s").getStatus());
	}
}
