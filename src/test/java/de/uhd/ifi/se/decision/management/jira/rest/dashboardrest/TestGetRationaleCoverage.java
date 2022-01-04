package de.uhd.ifi.se.decision.management.jira.rest.dashboardrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.rest.DashboardRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestGetRationaleCoverage extends TestSetUp {
	private DashboardRest dashboardRest;
	private HttpServletRequest request;
	private FilterSettings filterSettings;

	@Before
	public void setUp() {
		init();
		dashboardRest = new DashboardRest();
		ApplicationUser user = JiraUsers.BLACK_HEAD.getApplicationUser();
		request = new MockHttpServletRequest();
		request.setAttribute("user", user);
		filterSettings = new FilterSettings("TEST", "");
	}

	@Test
	public void testRequestValidFilterSettingsValid() {
		Response response = dashboardRest.getRationaleCoverage(request, filterSettings);
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
	}

	@Test
	public void testRequestValidFilterSettingsNull() {
		Response response = dashboardRest.getRationaleCoverage(request, null);
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
	}

	@Test
	public void testRequestNullFilterSettingsValid() {
		Response response = dashboardRest.getRationaleCoverage(null, filterSettings);
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
	}
}
