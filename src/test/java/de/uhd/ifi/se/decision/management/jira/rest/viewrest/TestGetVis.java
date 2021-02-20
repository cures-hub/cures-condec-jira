package de.uhd.ifi.se.decision.management.jira.rest.viewrest;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.jira.user.ApplicationUser;
import com.sun.jersey.api.client.ClientResponse.Status;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.config.AuthenticationManager;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.rest.ViewRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestGetVis extends TestSetUp {

	private ViewRest viewRest;
	private FilterSettings filterSettings;
	private HttpServletRequest request;

	@Before
	public void setUp() {
		viewRest = new ViewRest();
		init();
		request = new MockHttpServletRequest();
		String jql = "?jql=issuetype%20%3D%20Issue";
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		filterSettings = new FilterSettings("TEST", jql, user);
		request.setAttribute("user", user);
	}

	@Test
	public void testRequestValidProjectKeyNull() {
		filterSettings.setProjectKey(null);
		assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getVis(request, filterSettings).getStatus());
	}

	@Test
	public void testRequestNullFilterSettingsNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getVis(null, null).getStatus());
	}

	@Test
	public void testRequestNullFilerSettingsValidSelectedElementNotExisting() {
		filterSettings.setSelectedElement("NotTEST");
		assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getVis(null, filterSettings).getStatus());
	}

	@Test
	public void testRequestNullFilterSettingsElementExisting() {
		filterSettings.setSelectedElement("TEST-14");
		assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getVis(null, filterSettings).getStatus());
	}

	@Test
	public void testRequestFilledFilterSettingsFilledElementFilled() {
		assertNotNull(AuthenticationManager.getUser(request));
		filterSettings.setSelectedElement("TEST-1");
		assertEquals(Status.OK.getStatusCode(), viewRest.getVis(request, filterSettings).getStatus());
	}

	@Test
	public void testFilterSettingsValidCiaRequest() {
		assertNotNull(AuthenticationManager.getUser(request));
		filterSettings.setSelectedElement("TEST-1");
		filterSettings.setCiaRequest(true);
		assertEquals(Status.OK.getStatusCode(), viewRest.getVis(request, filterSettings).getStatus());
	}
}
