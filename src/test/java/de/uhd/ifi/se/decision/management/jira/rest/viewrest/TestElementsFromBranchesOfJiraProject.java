package de.uhd.ifi.se.decision.management.jira.rest.viewrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;
import org.ofbiz.core.entity.GenericEntityException;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.rest.ViewRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import de.uhd.ifi.se.decision.management.jira.view.diffviewer.DiffViewer;

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
	public void testEmptyIssueKey() throws GenericEntityException {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getAllFeatureBranchesTree("").getStatus());
	}

	@Test
	public void testUnknownProjectKey() throws GenericEntityException {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getAllFeatureBranchesTree("HOUDINI").getStatus());
	}

	@Test
	public void testExistingProjectKey() throws GenericEntityException {
		assertEquals(200, viewRest.getAllFeatureBranchesTree("TEST").getStatus());
		Object receivedEntity = viewRest.getAllFeatureBranchesTree("TEST").getEntity();

		Object expectedEntity = new DiffViewer(null);
		assertEquals(expectedEntity.getClass(), receivedEntity.getClass());
	}
}
