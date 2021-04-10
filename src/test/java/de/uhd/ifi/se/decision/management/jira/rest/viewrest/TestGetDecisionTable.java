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

public class TestGetDecisionTable extends TestSetUp {

	private ViewRest viewRest;
	private HttpServletRequest request;

	@Before
	public void setUp() {
		init();
		viewRest = new ViewRest();
		request = new MockHttpServletRequest();
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		request.setAttribute("user", user);
	}

	@Test
	public void testRequestNullFilterSettingsNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getDecisionTable(null, null).getStatus());
	}

	@Test
	public void testRequestValidFilterSettingsNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getDecisionTable(request, null).getStatus());
	}

	@Test
	public void testRequestValidFilterSettingsValid() {
		FilterSettings filterSettings = new FilterSettings("TEST", "");
		filterSettings.setSelectedElement("TEST-2");
		assertEquals(Status.OK.getStatusCode(), viewRest.getDecisionTable(request, filterSettings).getStatus());
	}

	@Test
	public void testRequestValidFilterSettingsWithoutSelectedElement() {
		FilterSettings filterSettings = new FilterSettings("TEST", "");
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				viewRest.getDecisionTable(request, filterSettings).getStatus());
	}

	@Test
	public void testRequestValidFilterSettingsWithInvalidProjectKey() {
		FilterSettings filterSettings = new FilterSettings("NonExistingProject", "");
		filterSettings.setSelectedElement("TEST-2");
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				viewRest.getDecisionTable(request, filterSettings).getStatus());
	}

	@Test
	public void testDecisionTableCriteriaRequestNullProjectKeyNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getDecisionTableCriteria(null, null).getStatus());
	}

	@Test
	public void testDecisionTableCriteriaRequestValidProjectKeyValid() {
		assertEquals(Status.OK.getStatusCode(), viewRest.getDecisionTableCriteria(request, "TEST").getStatus());
	}

	@Test
	public void testDecisionTableCriteriaRequestNullProjectKeyValid() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getDecisionTableCriteria(null, "TEST").getStatus());
	}

	@Test
	public void testDecisionTableCriteriaRequestValidProjectKeyInvalid() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				viewRest.getDecisionTableCriteria(request, "NonExistingProject").getStatus());
	}
}
