package de.uhd.ifi.se.decision.management.jira.rest.decisionguidancerest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.rest.DecisionGuidanceRest;

public class TestRemoveRecommendations extends TestSetUp {
	private DecisionGuidanceRest decisionGuidanceRest;
	private HttpServletRequest request;

	@Before
	public void setUp() {
		init();
		decisionGuidanceRest = new DecisionGuidanceRest();
		request = new MockHttpServletRequest();
	}

	@Test
	public void testValid() {
		assertEquals(Status.OK.getStatusCode(),
				decisionGuidanceRest.removeRecommendationsForKnowledgeElement(request, 1L).getStatus());
	}

	@Test
	public void testInvalidJiraIssueId() {
		assertEquals(Status.NOT_FOUND.getStatusCode(),
				decisionGuidanceRest.removeRecommendationsForKnowledgeElement(request, 119283L).getStatus());
	}

	@Test
	public void testRequestNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				decisionGuidanceRest.removeRecommendationsForKnowledgeElement(null, 1L).getStatus());
	}

	@Test
	public void testJiraIssueIdNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				decisionGuidanceRest.removeRecommendationsForKnowledgeElement(request, null).getStatus());
	}
}
