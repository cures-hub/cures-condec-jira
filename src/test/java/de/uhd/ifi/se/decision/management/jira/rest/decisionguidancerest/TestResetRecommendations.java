package de.uhd.ifi.se.decision.management.jira.rest.decisionguidancerest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.rest.DecisionGuidanceRest;

public class TestResetRecommendations extends TestSetUp {
	private DecisionGuidanceRest decisionGuidanceRest;
	private HttpServletRequest request;

	@Before
	public void setUp() {
		init();
		decisionGuidanceRest = new DecisionGuidanceRest();
		request = new MockHttpServletRequest();
	}

	@Test
	public void testResetRecommendations() {
		assertEquals(Status.OK.getStatusCode(),
				decisionGuidanceRest.resetRecommendationsForKnowledgeElement(request, 1L).getStatus());
	}

	@Test
	public void testResetRecommendationsInvalidIssueID() {
		assertEquals(Status.NOT_FOUND.getStatusCode(),
				decisionGuidanceRest.resetRecommendationsForKnowledgeElement(request, 119283L).getStatus());
	}

	@Test
	public void testResetRecommendationRequestNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				decisionGuidanceRest.resetRecommendationsForKnowledgeElement(null, 1L).getStatus());
	}

	@Test
	public void testResetRecommendationsJiraIssueIDNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				decisionGuidanceRest.resetRecommendationsForKnowledgeElement(request, null).getStatus());
	}
}
