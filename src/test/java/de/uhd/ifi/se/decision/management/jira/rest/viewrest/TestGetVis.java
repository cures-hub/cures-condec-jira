package de.uhd.ifi.se.decision.management.jira.rest.viewrest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.jira.user.ApplicationUser;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.config.AuthenticationManager;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.impl.FilterSettingsImpl;
import de.uhd.ifi.se.decision.management.jira.rest.ViewRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestGetVis extends TestSetUp {

	private ViewRest viewRest;
	private FilterSettings filterSettings;
	protected HttpServletRequest request;

	private static final String INVALID_PROJECTKEY = "Decision knowledge elements cannot be shown since project key is invalid.";
	private static final String INVALID_ELEMENTS = "Visualization cannot be shown since element key is invalid.";
	private static final String INVALID_FILTER_SETTINGS = "The filter settings are null. Vis graph could not be created.";
	private static final String INVALID_REQUEST = "HttpServletRequest is null. Vis graph could not be created.";

	@Before
	public void setUp() {
		viewRest = new ViewRest();
		init();
		request = new MockHttpServletRequest();
		String jql = "project%20%3D%20CONDEC%20AND%20assignee%20%3D%20currentUser()%20AND%20resolution%20%3D%20Unresolved%20ORDER%20BY%20updated%20DESC";
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		filterSettings = new FilterSettingsImpl("TEST", jql, user);
		request.setAttribute("user", user);
	}

	@Test
	@NonTransactional
	public void testRequestNullFilterDataSettingsElementNull() {
		assertEquals(
				Response.status(Response.Status.INTERNAL_SERVER_ERROR)
						.entity(ImmutableMap.of("error", INVALID_ELEMENTS)).build().getEntity(),
				viewRest.getVis(null, null, null).getEntity());
	}

	@Test
	@NonTransactional
	public void testRequestNullFilerSettingsNullElementNotExisting() {
		assertEquals(
				Response.status(Response.Status.INTERNAL_SERVER_ERROR)
						.entity(ImmutableMap.of("error", INVALID_PROJECTKEY)).build().getEntity(),
				viewRest.getVis(null, null, "NotTEST").getEntity());
	}

	@Test
	@NonTransactional
	public void testRequestNullFilterSettingsNullElementExisting() {
		assertEquals(
				Response.status(Response.Status.INTERNAL_SERVER_ERROR)
						.entity(ImmutableMap.of("error", INVALID_FILTER_SETTINGS)).build().getEntity(),
				viewRest.getVis(null, null, "TEST-12").getEntity());
	}

	@Test
	@NonTransactional
	public void testRequestNullFilterSettingsFilledElementFilled() {
		assertEquals(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
				.entity(ImmutableMap.of("error", INVALID_REQUEST)).build().getEntity(),
				viewRest.getVis(null, filterSettings, "TEST-12").getEntity());
	}

	@Test
	@NonTransactional
	public void testRequestFilledFilterSettingsFilledElementFilled() {
		assertNotNull(AuthenticationManager.getUser(request));
		assertEquals(Response.Status.OK.getStatusCode(),
				viewRest.getVis(request, filterSettings, "TEST-12").getStatus());
	}
}
