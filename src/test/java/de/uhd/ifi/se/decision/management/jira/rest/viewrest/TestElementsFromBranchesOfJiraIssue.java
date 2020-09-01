package de.uhd.ifi.se.decision.management.jira.rest.viewrest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.exception.PermissionException;
import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.rest.ViewRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import de.uhd.ifi.se.decision.management.jira.view.diffviewer.DiffViewer;

public class TestElementsFromBranchesOfJiraIssue extends TestSetUpGit {
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
		ConfigPersistenceManager.setKnowledgeExtractedFromGit("", true);
		try {
			assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getFeatureBranchTree(request, "").getStatus());
		} catch (PermissionException e) {
			assertNull(e);
		}
	}

	@Test
	public void testUnknownIssueKey() {
		try {
			assertEquals(Status.BAD_REQUEST.getStatusCode(),
					viewRest.getFeatureBranchTree(request, "HOUDINI-1").getStatus());
		} catch (PermissionException e) {
			assertNull(e);
		}
	}

	@Test
	public void testGitExtractionDisabled() {
		try {
			ConfigPersistenceManager.setKnowledgeExtractedFromGit("TEST", false);
			assertEquals(200, viewRest.getFeatureBranchTree(request, "TEST-2").getStatus());
		} catch (PermissionException e) {
			assertNull(e);
		}
		ConfigPersistenceManager.setKnowledgeExtractedFromGit("TEST", true);
	}

	@Test
	public void testExistingIssueKey() {
		try {
			assertEquals(200, viewRest.getFeatureBranchTree(request, "TEST-2").getStatus());
			Object receivedEntity = viewRest.getFeatureBranchTree(request, "TEST-2").getEntity();
			Object expectedEntity = new DiffViewer(null);
			assertEquals(expectedEntity.getClass(), receivedEntity.getClass());
		} catch (PermissionException e) {
			assertNull(e);
		}
	}
}
