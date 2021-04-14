package de.uhd.ifi.se.decision.management.jira.rest.dashboardrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.rest.DashboardRest;

public class TestGetCodeCoverage extends TestSetUp {
	protected DashboardRest dashboardRest;
	protected HttpServletRequest request;

	@Before
	public void setUp() {
		init();
		dashboardRest = new DashboardRest();
		ApplicationUser user = JiraUsers.BLACK_HEAD.getApplicationUser();
		this.request = new MockHttpServletRequest();
		this.request.setAttribute("user", user);
	}

	@Test
	public void testGetCodeCoverage() {
		String projectKey = "TEST";
		FilterSettings filterSettings = new FilterSettings(projectKey, "");
		Response response = dashboardRest.getCodeCoverage(request, filterSettings);
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
	}

	@Test
	public void testGetCodeCoverageNull() {
		FilterSettings filterSettings = null;
		Response response = dashboardRest.getCodeCoverage(request, filterSettings);
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
	}

	@Test
	public void testGetCodeCoverageNoRequest() {
		String projectKey = "TEST";
		FilterSettings filterSettings = new FilterSettings(projectKey, "");
		Response response = dashboardRest.getCodeCoverage(null, filterSettings);
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
	}
}
