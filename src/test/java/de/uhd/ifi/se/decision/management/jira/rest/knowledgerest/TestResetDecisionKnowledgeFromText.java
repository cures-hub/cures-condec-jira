package de.uhd.ifi.se.decision.management.jira.rest.knowledgerest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.rest.KnowledgeRest;

public class TestResetDecisionKnowledgeFromText extends TestSetUp {
	private KnowledgeRest knowledgeRest;
	private HttpServletRequest request;

	@Before
	public void setUp() {
		init();
		knowledgeRest = new KnowledgeRest();
		request = new MockHttpServletRequest();
	}

	@Test
	public void testRequestValidJiraIssueValid() {
		assertEquals(Status.OK.getStatusCode(), knowledgeRest.resetDecisionKnowledgeFromText(request, 1L).getStatus());
	}

	@Test
	public void testRequestValidJiraIssueNotExisting() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				knowledgeRest.resetDecisionKnowledgeFromText(request, 119283L).getStatus());
	}

	@Test
	public void testRequestValidJiraIssueNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				knowledgeRest.resetDecisionKnowledgeFromText(request, null).getStatus());
	}

	@Test
	public void testRequestNullJiraIssueValid() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				knowledgeRest.resetDecisionKnowledgeFromText(null, 1L).getStatus());
	}
}
