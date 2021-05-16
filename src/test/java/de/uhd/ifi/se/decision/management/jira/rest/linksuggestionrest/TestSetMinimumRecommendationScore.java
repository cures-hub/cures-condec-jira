package de.uhd.ifi.se.decision.management.jira.rest.linksuggestionrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.rest.LinkRecommendationRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestSetMinimumRecommendationScore extends TestSetUp {

	protected HttpServletRequest request;
	protected LinkRecommendationRest configRest;

	@Before
	public void setUp() {
		init();
		configRest = new LinkRecommendationRest();
		request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
	}

	@Test
	public void testNegativeValue() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				configRest.setMinimumRecommendationScore(request, "TEST", -1).getStatus());
	}

	@Test
	public void testEdgeCaseZero() {
		assertEquals(Status.OK.getStatusCode(),
				configRest.setMinimumRecommendationScore(request, "TEST", 0).getStatus());
	}

	@Test
	public void testEdgeCaseOne() {
		assertEquals(Status.OK.getStatusCode(),
				configRest.setMinimumRecommendationScore(request, "TEST", 0.3).getStatus());
	}

	@Test
	public void testValidValue() {
		assertEquals(Status.OK.getStatusCode(),
				configRest.setMinimumRecommendationScore(request, "TEST", 1).getStatus());
	}

	@Test
	public void testValueGreaterOne() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				configRest.setMinimumRecommendationScore(request, "TEST", 2).getStatus());
	}

	@Test
	public void testInvalidProjectKey() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				configRest.setMinimumRecommendationScore(request, "", 0).getStatus());
	}
}