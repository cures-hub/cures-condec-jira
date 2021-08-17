package de.uhd.ifi.se.decision.management.jira.rest.viewrest;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.jira.user.ApplicationUser;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.rest.ViewRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import static org.junit.Assert.assertEquals;

public class TestGetMatrix extends TestSetUp {
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
	public void testRequestNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getMatrix(null, settings).getStatus());
	}

	@Test
	public void testFilterSettingsNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getMatrix(request, null).getStatus());
	}

	@Test
	public void testProjectKeyNonExistent() {
		settings.setProjectKey("NotTEST");
		assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getMatrix(request, settings).getStatus());
	}

	@Test
	public void testFilterSettingsValid() {
		assertEquals(Status.OK.getStatusCode(), viewRest.getMatrix(request, settings).getStatus());
	}

	@Test
	public void testFilterSettingsValidCiaRequest() {
		FilterSettings filterSettings = new FilterSettings("TEST", "");
		filterSettings.setLinkDistance(2);
		filterSettings.setSelectedElement("TEST-12");
		filterSettings.highlightChangeImpacts(true);
		assertEquals(Status.OK.getStatusCode(), viewRest.getMatrix(request, filterSettings).getStatus());
	}
}
