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

public class TestSetAddRecommendationDirectly extends TestSetUp {
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
	public void testSetAddRecommendationDirectlyValidProject() {
		assertEquals(Status.OK.getStatusCode(),
				decisionGuidanceRest.setAddRecommendationDirectly(request, "TEST", true).getStatus());
	}

	@Test
	public void testSetAddRecommendationDirectlyInvalidProject() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				decisionGuidanceRest.setAddRecommendationDirectly(request, "", true).getStatus());
	}
}
