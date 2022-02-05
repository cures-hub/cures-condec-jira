package de.uhd.ifi.se.decision.management.jira.rest.linkrecommendationrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.rest.LinkRecommendationRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestSetMaxRecommendations extends TestSetUp {
    
    private HttpServletRequest request;
	private LinkRecommendationRest linkRecommendationRest;

	@Before
	public void setUp() {
		init();
		linkRecommendationRest = new LinkRecommendationRest();
		request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
	}

	@Test
	public void testInvalidRequest() {
		assertEquals(Status.UNAUTHORIZED.getStatusCode(),
				linkRecommendationRest.setMaximumRecommendations(new MockHttpServletRequest(), "TEST", -1).getStatus());
	}

	@Test
	public void testNegativeValueMinusOne() {
		assertEquals(Status.OK.getStatusCode(),
				linkRecommendationRest.setMaximumRecommendations(request, "TEST", -1).getStatus());
	}

	@Test
	public void testNegativeValueLessThanMinusOne() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				linkRecommendationRest.setMaximumRecommendations(request, "TEST", -2).getStatus());
	}

    @Test
	public void testZero() {
		assertEquals(Status.OK.getStatusCode(),
				linkRecommendationRest.setMaximumRecommendations(request, "TEST", 0).getStatus());
	}

    @Test
	public void testPositiveValue() {
		assertEquals(Status.OK.getStatusCode(),
				linkRecommendationRest.setMaximumRecommendations(request, "TEST", 10).getStatus());
	}

    @Test
	public void testInvalidProjectKey() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				linkRecommendationRest.setMinimumRecommendationScore(request, "", 0).getStatus());
	}
}
