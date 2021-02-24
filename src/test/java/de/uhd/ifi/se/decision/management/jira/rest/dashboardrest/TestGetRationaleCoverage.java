package de.uhd.ifi.se.decision.management.jira.rest.dashboardrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.rest.DashboardRest;

public class TestGetRationaleCoverage extends TestSetUp {
	protected DashboardRest dashboardRest;
	protected HttpServletRequest request;
	private String projectKey;
	private String issueType;
	private String linkDistance;

	@Before
	public void setUp() {
		init();
		dashboardRest = new DashboardRest();
		this.issueType = "TEST";
		this.linkDistance = Integer.toString(3);
		ApplicationUser user = JiraUsers.BLACK_HEAD.getApplicationUser();
		this.request = new MockHttpServletRequest();
		this.request.setAttribute("user", user);
	}

	@Test
	public void testGetRationaleCoverage() {
		this.projectKey = "TEST";
		Response response = dashboardRest.getRationaleCoverage(request, projectKey, issueType, linkDistance);
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
	}

	public void testGetRationaleCoverageNull() {
		this.projectKey = null;
		Response response = dashboardRest.getRationaleCoverage(request, projectKey, issueType, linkDistance);
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
	}
}
