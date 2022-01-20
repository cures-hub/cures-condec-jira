package de.uhd.ifi.se.decision.management.jira.rest.linkrecommendationrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.rest.LinkRecommendationRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestGetLinkRecommendations extends TestSetUp {

	private HttpServletRequest request;
	private LinkRecommendationRest linkRecommendationRest;
	private FilterSettings filterSettings;

	@Before
	public void setUp() {
		init();

		linkRecommendationRest = new LinkRecommendationRest();
		request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
		filterSettings = new FilterSettings("TEST", "");
		filterSettings.setSelectedElementObject(KnowledgeElements.getTestKnowledgeElement());
	}

	@Test
	public void testRequestValidFilterSettingsValid() {
		assertEquals(Response.Status.OK.getStatusCode(),
				linkRecommendationRest.getLinkRecommendations(request, filterSettings).getStatus());
	}

	@Test
	public void testRequestValidFilterSettingsNull() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				linkRecommendationRest.getLinkRecommendations(request, null).getStatus());
	}

	@Test
	public void testRequestValidFilterSettingsNoSelectedElement() {
		filterSettings.setSelectedElementObject(null);
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				linkRecommendationRest.getLinkRecommendations(request, filterSettings).getStatus());
	}
}
