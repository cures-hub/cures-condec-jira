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
import de.uhd.ifi.se.decision.management.jira.filtering.impl.FilterSettingsImpl;
import de.uhd.ifi.se.decision.management.jira.rest.ViewRest;
import de.uhd.ifi.se.decision.management.jira.rest.impl.ViewRestImpl;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestGetDecisionGraphAndMatrix extends TestSetUp {
	private ViewRest viewRest;
	private FilterSettings settings;
	protected HttpServletRequest request;

	@Before
	public void setUp() {
		viewRest = new ViewRestImpl();
		init();
		ApplicationUser user = JiraUsers.BLACK_HEAD.getApplicationUser();
		request = new MockHttpServletRequest();
		request.setAttribute("user", user);
		settings = new FilterSettingsImpl();
	}

	@Test
	public void testDecisionGraphProjectKeyNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getDecisionGraph(request, null, null).getStatus());
	}

	@Test
	public void testDecisionGraphProjectKeyNonExistent() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				viewRest.getDecisionGraph(request, null, "NotTEST").getStatus());
	}

	@Test
	public void testDecisionGraphProjectKeyExistent() {
		assertEquals(200, viewRest.getDecisionGraph(request, settings, "TEST").getStatus());
	}

	@Test
	public void testDecisionGraphFilteredProjectKeyNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				viewRest.getDecisionGraph(request, settings, null).getStatus());
	}

	@Test
	public void testDecisionGraphFilteredProjectKeyNonExistent() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				viewRest.getDecisionGraph(request, settings, "NotTEST").getStatus());
	}

	@Test
	public void testDecisionGraphFilteredProjectKeyExistent() {
		assertEquals(200, viewRest.getDecisionGraph(request, settings, "TEST").getStatus());
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
