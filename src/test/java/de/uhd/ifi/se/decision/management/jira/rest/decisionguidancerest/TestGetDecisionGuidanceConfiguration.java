package de.uhd.ifi.se.decision.management.jira.rest.decisionguidancerest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.rest.DecisionGuidanceRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestGetDecisionGuidanceConfiguration extends TestSetUp {

	private HttpServletRequest request;
	private DecisionGuidanceRest decisionGuidanceRest;

	@Before
	public void setUp() {
		init();
		decisionGuidanceRest = new DecisionGuidanceRest();
		request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
	}

	@Test
	public void testRequestNullProjectKeyNull() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				decisionGuidanceRest.getDecisionGuidanceConfiguration(null, null).getStatus());
	}

	@Test
	public void testRequestValidProjectKeyEmpty() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				decisionGuidanceRest.getDecisionGuidanceConfiguration(request, "").getStatus());
	}

	@Test
	public void testRequestValidProjectKeyInvalid() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				decisionGuidanceRest.getDecisionGuidanceConfiguration(request, "InvalidKey").getStatus());
	}

	@Test
	public void testRequestValidProjectKeyValid() {
		assertEquals(Response.Status.OK.getStatusCode(),
				decisionGuidanceRest.getDecisionGuidanceConfiguration(request, "TEST").getStatus());
	}
}
