package de.uhd.ifi.se.decision.management.jira.rest.viewrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.rest.ViewRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestGetEvolutionData extends TestSetUp {

	private ViewRest viewRest;
	private FilterSettings filterSettings;
	protected HttpServletRequest request;

	@Before
	public void setUp() {
		viewRest = new ViewRest();
		init();
		ApplicationUser user = JiraUsers.BLACK_HEAD.getApplicationUser();
		request = new MockHttpServletRequest();
		request.setAttribute("user", user);
		filterSettings = new FilterSettings("TEST", "");
	}

	@Test
	public void testRequestNullFilterSettingsNull() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				viewRest.getEvolutionData(null, null, false, false).getStatus());
	}

	@Test
	public void testRequestNullFilterSettingsFilled() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				viewRest.getEvolutionData(null, filterSettings, false, false).getStatus());
	}

	@Test
	public void testRequestFilledSettingsNull() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				viewRest.getEvolutionData(request, null, false, false).getStatus());
	}

	@Test
	public void testRequestFilledSettingsFilled() {
		assertEquals(Response.Status.OK.getStatusCode(),
				viewRest.getEvolutionData(request, filterSettings, true, true).getStatus());
	}
}
