package de.uhd.ifi.se.decision.management.jira.rest.dashboardrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.rest.DashboardRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class testRequirementsAndCodeFiles extends TestSetUpGit {
	private DashboardRest dashboardRest;
	protected HttpServletRequest request;

	@Override
	@Before
	public void setUp() {
		dashboardRest = new DashboardRest();
		init();
		ApplicationUser user = JiraUsers.BLACK_HEAD.getApplicationUser();
		request = new MockHttpServletRequest();
		request.setAttribute("user", user);
	}

	@Test
	public void testRequestNull() {
		ConfigPersistenceManager.setKnowledgeExtractedFromGit("", true);
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
			dashboardRest.getRequirementsAndCodeFiles(null, null).getStatus());
	}

	@Test
	public void testIssueKeyNull() {
		ConfigPersistenceManager.setKnowledgeExtractedFromGit("", true);
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
			dashboardRest.getRequirementsAndCodeFiles(request, null).getStatus());
	}

	@Test
	public void testEmptyIssueKey() {
		ConfigPersistenceManager.setKnowledgeExtractedFromGit("", true);
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
			dashboardRest.getRequirementsAndCodeFiles(request, "").getStatus());
	}

	@Test
	public void testUnknownIssueKey() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
			dashboardRest.getRequirementsAndCodeFiles(request, "HOUDINI-1").getStatus());
	}

	@Test
	public void testGitExtractionDisabled() {
		ConfigPersistenceManager.setKnowledgeExtractedFromGit("TEST", false);
		assertEquals(Status.SERVICE_UNAVAILABLE.getStatusCode(),
			dashboardRest.getRequirementsAndCodeFiles(request, "TEST-2").getStatus());
		ConfigPersistenceManager.setKnowledgeExtractedFromGit("TEST", true);
	}

	@Test
	@NonTransactional
	public void testExistingIssueKey() {
		assertEquals(Status.OK.getStatusCode(),
			dashboardRest.getRequirementsAndCodeFiles(request, "TEST-2").getStatus());
	}
}
