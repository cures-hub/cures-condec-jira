package de.uhd.ifi.se.decision.management.jira.rest.decisionguidancerest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.rest.DecisionGuidanceRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestGetRecommendationEvaluation extends TestSetUp {

	private DecisionGuidanceRest decisionGuidanceRest;
	private HttpServletRequest request;

	@Before
	public void setUp() {
		init();
		decisionGuidanceRest = new DecisionGuidanceRest();
		request = new MockHttpServletRequest();
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		request.setAttribute("user", user);
	}

	@Test
	public void testGetRecommendationEvaluationActiveSource() {
		decisionGuidanceRest.setProjectSource(request, "TEST", "TEST", true);
		assertEquals(Status.OK.getStatusCode(), decisionGuidanceRest
				.getRecommendationEvaluation(request, "TEST", "keyword", "TEST", 5, 2, "i").getStatus());
		decisionGuidanceRest.setProjectSource(request, "TEST", "TEST", false);
	}

	@Test
	public void testGetRecommendationEvaluationINVALIDISSUE() {
		assertEquals(Status.NOT_FOUND.getStatusCode(), decisionGuidanceRest
				.getRecommendationEvaluation(request, "TEST", "keyword", "TEST", 5, -12354, "s").getStatus());
	}

	@Test
	public void testGetRecommendationEvaluationInvalidProject() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), decisionGuidanceRest
				.getRecommendationEvaluation(request, "", "keyword", "TEST", 5, 1, "s").getStatus());
	}

	@Test
	public void testGetRecommendationEvaluationRequestNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), decisionGuidanceRest
				.getRecommendationEvaluation(null, "TEST", "keyword", "TEST", 5, 1, "s").getStatus());
	}
}
