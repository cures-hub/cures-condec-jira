package de.uhd.ifi.se.decision.management.jira.rest.viewrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
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
	public void testGetDecisionTableDataRequestNullLocationNullElementKeyNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getDecisionTable(null, 0, null, null).getStatus());
	}

	@Test
	public void testGetDecisionTableData() {
		assertEquals(Status.OK.getStatusCode(), viewRest
				.getDecisionTable(request, 2, DocumentationLocation.JIRAISSUE.getIdentifier(), "TEST").getStatus());
	}

	@Test
	public void testDecisionTableCriteriaRequestNullElementKeyNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getDecisionTableCriteria(null, null).getStatus());
	}

	@Test
	public void testDecisionTableCriteria() {
		assertEquals(Status.OK.getStatusCode(), viewRest.getDecisionTableCriteria(request, "TEST").getStatus());
	}
}
