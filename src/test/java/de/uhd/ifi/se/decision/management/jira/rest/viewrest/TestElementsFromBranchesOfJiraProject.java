package de.uhd.ifi.se.decision.management.jira.rest.viewrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.rest.ViewRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestElementsFromBranchesOfJiraProject extends TestSetUpGit {
	private ViewRest viewRest;
	protected HttpServletRequest request;

	@Override
	@Before
	public void setUp() {
		viewRest = new ViewRest();
		init();
		ApplicationUser user = JiraUsers.BLACK_HEAD.getApplicationUser();
		request = new MockHttpServletRequest();
		request.setAttribute("user", user);
	}

	@Test
	public void testEmptyIssueKey() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getElementsFromAllBranchesOfProject("").getStatus());
	}

	@Test
	public void testUnknownProjectKey() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				viewRest.getElementsFromAllBranchesOfProject("HOUDINI").getStatus());
	}

	@Test
	public void testExistingProjectKey() {
		assertEquals(Status.OK.getStatusCode(), viewRest.getElementsFromAllBranchesOfProject("TEST").getStatus());
	}

	@Test
	public void testGitExtractionDisabled() {
		assertEquals(Status.SERVICE_UNAVAILABLE.getStatusCode(),
				viewRest.getElementsFromAllBranchesOfProject("TEST").getStatus());
	}
}
