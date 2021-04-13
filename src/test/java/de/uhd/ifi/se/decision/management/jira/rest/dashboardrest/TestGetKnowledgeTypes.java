package de.uhd.ifi.se.decision.management.jira.rest.dashboardrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.rest.DashboardRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestGetKnowledgeTypes extends TestSetUp {
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
	public void testGetKnowledgeTypes() {
		Response response = dashboardRest.getDocumentationLocations(request);
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
	}
}
