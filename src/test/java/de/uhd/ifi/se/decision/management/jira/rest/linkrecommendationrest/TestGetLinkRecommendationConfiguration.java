package de.uhd.ifi.se.decision.management.jira.rest.linkrecommendationrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.rest.LinkRecommendationRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestGetLinkRecommendationConfiguration extends TestSetUp {

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
	public void testRequestNullProjectKeyNull() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				linkRecommendationRest.getLinkRecommendationConfiguration(null, null).getStatus());
	}

	@Test
	public void testRequestValidProjectKeyEmpty() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				linkRecommendationRest.getLinkRecommendationConfiguration(request, "").getStatus());
	}

	@Test
	public void testRequestValidProjectKeyInvalid() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				linkRecommendationRest.getLinkRecommendationConfiguration(request, "InvalidKey").getStatus());
	}

	@Test
	public void testRequestValidProjectKeyValid() {
		assertEquals(Response.Status.OK.getStatusCode(),
				linkRecommendationRest.getLinkRecommendationConfiguration(request, "TEST").getStatus());
	}
}
