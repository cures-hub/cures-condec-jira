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

public class TestGetGeneralMetrics extends TestSetUp {

	protected DashboardRest dashboardRest;
	protected HttpServletRequest request;
	private String projectKey;

	@Before
	public void setUp() {
		init();
		dashboardRest = new DashboardRest();
		this.projectKey = "TEST";
		ApplicationUser user = JiraUsers.BLACK_HEAD.getApplicationUser();
		request = new MockHttpServletRequest();
		request.setAttribute("user", user);
	}

	@Test
	public void testGetGeneralMetrics() {
		Response response = dashboardRest.getGeneralMetrics(request, projectKey);
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
	}
}
