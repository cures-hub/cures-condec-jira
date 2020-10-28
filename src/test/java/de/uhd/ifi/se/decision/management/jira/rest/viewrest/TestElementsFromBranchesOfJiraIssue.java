package de.uhd.ifi.se.decision.management.jira.rest.viewrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

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
	public void testRequestNull() {
		ConfigPersistenceManager.setKnowledgeExtractedFromGit("", true);
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				viewRest.getElementsOfFeatureBranchForJiraIssue(null, null).getStatus());
	}

	@Test
	public void testIssueKeyNull() {
		ConfigPersistenceManager.setKnowledgeExtractedFromGit("", true);
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				viewRest.getElementsOfFeatureBranchForJiraIssue(request, null).getStatus());
	}

	@Test
	public void testEmptyIssueKey() {
		ConfigPersistenceManager.setKnowledgeExtractedFromGit("", true);
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				viewRest.getElementsOfFeatureBranchForJiraIssue(request, "").getStatus());
	}

	@Test
	public void testUnknownIssueKey() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				viewRest.getElementsOfFeatureBranchForJiraIssue(request, "HOUDINI-1").getStatus());
	}

	@Test
	public void testGitExtractionDisabled() {
		ConfigPersistenceManager.setKnowledgeExtractedFromGit("TEST", false);
		assertEquals(Status.SERVICE_UNAVAILABLE.getStatusCode(),
				viewRest.getElementsOfFeatureBranchForJiraIssue(request, "TEST-2").getStatus());
		ConfigPersistenceManager.setKnowledgeExtractedFromGit("TEST", true);
	}

	@Test
	@Ignore
	public void testExistingIssueKey() {
		assertEquals(200, viewRest.getElementsOfFeatureBranchForJiraIssue(request, "TEST-2").getStatus());
		Object receivedEntity = viewRest.getElementsOfFeatureBranchForJiraIssue(request, "TEST-2").getEntity();
		Object expectedEntity = new DiffViewer(null);
		assertEquals(expectedEntity.getClass(), receivedEntity.getClass());
	}
}
