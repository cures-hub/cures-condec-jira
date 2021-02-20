package de.uhd.ifi.se.decision.management.jira.rest.viewrest;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.jira.user.ApplicationUser;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.rest.ViewRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import org.junit.Before;
import org.junit.Test;
import org.ofbiz.core.entity.GenericEntityException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import static org.junit.Assert.assertEquals;

public class TestGetTreant extends TestSetUp {

	private ViewRest viewRest;
	private FilterSettings filterSettings;

	@Before
	public void setUp() {
		init();
		viewRest = new ViewRest();
		filterSettings = new FilterSettings("TEST", null);
	}

	@Test
	public void testRequestNullElementKeyNullFilterSettingsNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getTreant(null, null).getStatus());
	}

	@Test
	public void testRequestNullElementNotExists() throws GenericEntityException {
		filterSettings.setSelectedElement("NotTEST-123");
		assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getTreant(null, filterSettings).getStatus());
	}

	@Test
	public void testRequestValidFilterSettingsNull() {
		HttpServletRequest request = new MockHttpServletRequest();
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		request.setAttribute("user", user);
		assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getTreant(request, null).getStatus());
	}

	@Test
	public void testRequestFilledElementNotExists() {
		HttpServletRequest request = new MockHttpServletRequest();
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		request.setAttribute("user", user);
		filterSettings.setSelectedElement("NotTEST-123");
		assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getTreant(request, filterSettings).getStatus());
	}

	@Test
	public void testRequestFilledElementExistsFilterSettingsNull() {
		HttpServletRequest request = new MockHttpServletRequest();
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		request.setAttribute("user", user);
		filterSettings.setSelectedElement("TEST-12");
		assertEquals(Status.OK.getStatusCode(), viewRest.getTreant(request, filterSettings).getStatus());
	}

	@Test
	public void testRequestFilledElementExistsFilterSettingsFilled() {
		HttpServletRequest request = new MockHttpServletRequest();
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		request.setAttribute("user", user);
		FilterSettings filterSettings = new FilterSettings("TEST", "");
		filterSettings.setLinkDistance(2);
		filterSettings.setSelectedElement("TEST-12");
		assertEquals(Status.OK.getStatusCode(), viewRest.getTreant(request, filterSettings).getStatus());
	}

	@Test
	public void testFilterSettingsValidCiaRequest() {
		HttpServletRequest request = new MockHttpServletRequest();
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		request.setAttribute("user", user);
		FilterSettings filterSettings = new FilterSettings("TEST", "");
		filterSettings.setLinkDistance(2);
		filterSettings.setSelectedElement("TEST-12");
		filterSettings.setCiaRequest(true);
		assertEquals(Status.OK.getStatusCode(), viewRest.getTreant(request, filterSettings).getStatus());
	}
}
