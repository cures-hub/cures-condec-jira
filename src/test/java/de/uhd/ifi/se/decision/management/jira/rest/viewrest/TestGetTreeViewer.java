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

	// @Test
	// @NonTransactional
	// public void testProjectKeyNullKnowledgeTypeNull() {
	// assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getTreeViewer(null,
	// null).getStatus());
	// }
	//
	// @Test
	// @NonTransactional
	// public void testProjectKeyNonExistentKnowledgeTypeNull() {
	// assertEquals(Status.BAD_REQUEST.getStatusCode(),
	// viewRest.getTreeViewer("NotExistingProjectKey", null).getStatus());
	// }
	//
	// @Test
	// @NonTransactional
	// public void testProjectKeyExistentKnowledgeTypeNull() {
	// assertEquals(200, viewRest.getTreeViewer("TEST", null).getStatus());
	// }
	//
	// @Test
	// public void testProjectKeyExistentKnowledgeTypeEmpty() {
	// assertEquals(200, viewRest.getTreeViewer("TEST", "").getStatus());
	// }
	//
	// @Test
	// public void testProjectKeyExistentKnowledgeTypeFilled() {
	// assertEquals(200, viewRest.getTreeViewer("TEST", "Issue").getStatus());
	// }

	@Test
	public void testRequestNullJiraIssueKeyNullFilterSettingsNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				viewRest.getTreeViewer(null, null).getStatus());
	}

	@Test
	public void testRequestValidFilterSettingsValid() {
		assertEquals(Status.OK.getStatusCode(),
				viewRest.getTreeViewer(request, filterSettings).getStatus());
	}

	@Test
	public void testRequestValidJiraIssueKeyValidFilterSettingsValid() {
		filterSettings.setSelectedElement("TEST-1");
		assertEquals(Status.OK.getStatusCode(),
				viewRest.getTreeViewer(request, filterSettings).getStatus());
	}

	@Test
	public void testRequestValidJiraIssueKeyValidFilterSettingsInvalid() {
		FilterSettings filterSettings = new FilterSettings("NOTEXISTINGPROJECTKEY", "");
		filterSettings.setSelectedElement("NOTEXISTINGPROJECTKEY-5");
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				viewRest.getTreeViewer(request, filterSettings).getStatus());
	}
}
