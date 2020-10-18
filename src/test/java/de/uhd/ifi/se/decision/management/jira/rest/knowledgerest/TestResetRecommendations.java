package de.uhd.ifi.se.decision.management.jira.rest.knowledgerest;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.rest.KnowledgeRest;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import static org.junit.Assert.assertEquals;

public class TestResetRecommendations extends TestSetUp {
	private KnowledgeRest knowledgeRest;
	private HttpServletRequest request;

	@Before
	public void setUp() {
		init();
		knowledgeRest = new KnowledgeRest();
		request = new MockHttpServletRequest();
	}

	@Test
	public void testResetRecommendations() {
		assertEquals(Status.OK.getStatusCode(),
				knowledgeRest.resetRecommendationsForKnowledgeElement(request, 1L).getStatus());
	}

	@Test
	public void testResetRecommendationsInvalidIssueID() {
		assertEquals(Status.NOT_FOUND.getStatusCode(),
			knowledgeRest.resetRecommendationsForKnowledgeElement(request, 119283L).getStatus());
	}

	@Test
	public void testResetRecommendationRequestNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
			knowledgeRest.resetRecommendationsForKnowledgeElement(null, 1L).getStatus());
	}
	@Test
	public void testResetRecommendationsJiraIssueIDNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
			knowledgeRest.resetRecommendationsForKnowledgeElement(request, null).getStatus());
	}
}
