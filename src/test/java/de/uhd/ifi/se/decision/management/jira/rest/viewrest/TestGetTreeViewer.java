package de.uhd.ifi.se.decision.management.jira.rest.viewrest;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.jira.user.ApplicationUser;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.rest.ViewRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import static org.junit.Assert.assertEquals;

public class TestGetTreeViewer extends TestSetUp {
	private ViewRest viewRest;
	private HttpServletRequest request;
	private FilterSettings filterSettings;

	@Before
	public void setUp() {
		init();
		viewRest = new ViewRest();
		request = new MockHttpServletRequest();
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		request.setAttribute("user", user);
		filterSettings = new FilterSettings("TEST", "");
	}

	@Test
	@NonTransactional
	public void testRequestNullFilterSettingsNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getTreeViewer(null, null).getStatus());
	}

	@Test
	@NonTransactional
	public void testRequestValidFilterSettingsNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getTreeViewer(request, null).getStatus());
	}

	@Test
	@NonTransactional
	public void testRequestNullFilterSettingsValid() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getTreeViewer(null, filterSettings).getStatus());
	}

	@Test
	public void testRequestValidProjectKeyNullInFilterSettings() {
		filterSettings.setProjectKey(null);
		assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getTreeViewer(request, filterSettings).getStatus());
	}

	@Test
	public void testRequestValidFilterSettingsValid() {
		assertEquals(Status.OK.getStatusCode(), viewRest.getTreeViewer(request, filterSettings).getStatus());
	}

	@Test
	public void testRequestValidJiraIssueKeyValidFilterSettingsValid() {
		filterSettings.setSelectedElement("TEST-1");
		assertEquals(Status.OK.getStatusCode(), viewRest.getTreeViewer(request, filterSettings).getStatus());
	}

	@Test
	public void testRequestValidJiraIssueKeyValidFilterSettingsInvalid() {
		FilterSettings filterSettings = new FilterSettings("NOTEXISTINGPROJECTKEY", "");
		filterSettings.setSelectedElement("NOTEXISTINGPROJECTKEY-5");
		assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getTreeViewer(request, filterSettings).getStatus());
	}

	@Test
	public void testFilterSettingsValidCiaRequest() {
		FilterSettings filterSettings = new FilterSettings("TEST", "");
		filterSettings.setLinkDistance(2);
		filterSettings.setSelectedElement("TEST-12");
		filterSettings.setCiaRequest(true);
		assertEquals(Status.OK.getStatusCode(), viewRest.getTreeViewer(request, filterSettings).getStatus());
	}
}
