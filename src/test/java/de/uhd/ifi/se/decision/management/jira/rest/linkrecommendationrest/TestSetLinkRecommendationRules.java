package de.uhd.ifi.se.decision.management.jira.rest.linkrecommendationrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.LinkRecommendationConfiguration;
import de.uhd.ifi.se.decision.management.jira.rest.LinkRecommendationRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestSetLinkRecommendationRules extends TestSetUp {

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
	public void testRequestNullProjectKeyNullRulesNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				linkRecommendationRest.setLinkRecommendationRules(null, null, null).getStatus());
	}

	@Test
	public void testRequestValidProjectKeyNullRulesNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				linkRecommendationRest.setLinkRecommendationRules(request, null, null).getStatus());
	}

	@Test
	public void testRequestValidProjectKeyValidRulesValid() {
		assertEquals(Status.OK.getStatusCode(), linkRecommendationRest.setLinkRecommendationRules(request, "TEST",
				LinkRecommendationConfiguration.getAllContextInformationProviders()).getStatus());
	}
}