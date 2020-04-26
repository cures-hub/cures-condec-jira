package de.uhd.ifi.se.decision.management.jira.rest.viewrest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.jira.user.ApplicationUser;
import com.sun.jersey.api.client.ClientResponse.Status;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.config.AuthenticationManager;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.rest.ViewRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

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
	public void testRequestNullFilterDataSettingsElementNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getVis(null, null, null).getStatus());
	}

	@Test
	public void testRequestNullFilerSettingsNullElementNotExisting() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getVis(null, null, "NotTEST").getStatus());
	}

	@Test
	public void testRequestNullFilterSettingsNullElementExisting() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getVis(null, null, "TEST-14").getStatus());
	}

	@Test
	public void testRequestNullFilterSettingsFilledElementFilled() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getVis(null, filterSettings, "TEST-14").getStatus());
	}

	@Test
	public void testRequestFilledFilterSettingsFilledElementFilled() {
		assertNotNull(AuthenticationManager.getUser(request));
		assertEquals(Status.OK.getStatusCode(), viewRest.getVis(request, filterSettings, "TEST-1").getStatus());
	}
}
