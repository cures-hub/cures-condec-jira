package de.uhd.ifi.se.decision.management.jira.rest.viewrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.rest.ViewRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestGetDecisionGraphAndMatrix extends TestSetUp {
	private ViewRest viewRest;
	private FilterSettings settings;
	protected HttpServletRequest request;

	@Before
	public void setUp() {
		viewRest = new ViewRest();
		init();
		ApplicationUser user = JiraUsers.BLACK_HEAD.getApplicationUser();
		request = new MockHttpServletRequest();
		request.setAttribute("user", user);
		settings = new FilterSettings("TEST", "");
	}

	@Test
	public void testDecisionGraphFilterSettingsNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getDecisionGraph(request, null).getStatus());
	}

	@Test
	public void testDecisionGraphFilterSettingsValid() {
		assertEquals(200, viewRest.getDecisionGraph(request, settings).getStatus());
	}

	@Test
	public void testDecisionGraphFilteredProjectKeyNull() {
		settings.setProjectKey(null);
		assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getDecisionGraph(request, settings).getStatus());
	}

	@Test
	public void testDecisionGraphFilteredProjectKeyNonExistent() {
		settings.setProjectKey("NotTEST");
		assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getDecisionGraph(request, settings).getStatus());
	}

	@Test
	public void testDecisionMatrixProjectKeyNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getDecisionMatrix(request, null).getStatus());
	}

	@Test
	public void testDecisionMatrixProjectKeyNonExistent() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getDecisionMatrix(request, "NotTEST").getStatus());
	}

	@Test
	public void testDecisionMatrixProjectKeyExistent() {
		assertEquals(200, viewRest.getDecisionMatrix(request, "TEST").getStatus());
	}
}
