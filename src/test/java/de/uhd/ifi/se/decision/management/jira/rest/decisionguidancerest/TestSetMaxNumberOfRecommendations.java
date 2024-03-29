package de.uhd.ifi.se.decision.management.jira.rest.decisionguidancerest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.rest.DecisionGuidanceRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestSetMaxNumberOfRecommendations extends TestSetUp {
	protected HttpServletRequest request;
	protected DecisionGuidanceRest decisionGuidanceRest;

	@Before
	public void setUp() {
		init();
		decisionGuidanceRest = new DecisionGuidanceRest();
		request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
	}

	@Test
	public void testSetMaxRecommendationsValid() {
		assertEquals(Status.OK.getStatusCode(),
				decisionGuidanceRest.setMaxNumberOfRecommendations(request, "TEST", 20).getStatus());
	}

	@Test
	public void testSetMaxRecommendationsInvalidValue() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				decisionGuidanceRest.setMaxNumberOfRecommendations(request, "TEST", -20).getStatus());
	}

	@Test
	public void testSetMaxRecommendationsInvalidProject() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				decisionGuidanceRest.setMaxNumberOfRecommendations(request, "", 20).getStatus());
	}
}
