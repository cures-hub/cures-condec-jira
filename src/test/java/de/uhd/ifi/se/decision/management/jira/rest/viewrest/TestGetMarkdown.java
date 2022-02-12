package de.uhd.ifi.se.decision.management.jira.rest.viewrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.rest.ViewRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestGetMarkdown extends TestSetUp {
	private ViewRest viewRest;
	private HttpServletRequest request;

	@Before
	public void setUp() {
		init();
		viewRest = new ViewRest();
		request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
	}

	@Test
	public void testRequestNullFilterSettingsNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getMarkdown(null, null).getStatus());
	}

	@Test
	public void testRequestValidFilterSettingsNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getMarkdown(request, null).getStatus());
	}

	@Test
	public void testRequestValidFilterSettingsProjectKeyInvalid() {
		FilterSettings filterSettings = new FilterSettings(null, "");
		Response response = viewRest.getMarkdown(request, filterSettings);
		assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
	}

	@Test
	public void testRequestValidFilterSettingsValidWithSelectedElement() {
		FilterSettings filterSettings = new FilterSettings("TEST", "");
		filterSettings.setSelectedElement("TEST-1");
		Response response = viewRest.getMarkdown(request, filterSettings);
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
	}

	@Test
	public void testRequestValidFilterSettingsValidWithoutSelectedElement() {
		FilterSettings filterSettings = new FilterSettings("TEST", "");
		filterSettings.setLinkDistance(0);
		Response response = viewRest.getMarkdown(request, filterSettings);
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
	}
}