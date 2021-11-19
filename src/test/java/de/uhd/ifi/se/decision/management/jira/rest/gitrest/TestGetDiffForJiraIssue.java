package de.uhd.ifi.se.decision.management.jira.rest.gitrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.git.config.GitConfiguration;
import de.uhd.ifi.se.decision.management.jira.git.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.rest.GitRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestGetDiffForJiraIssue extends TestSetUpGit {
	private GitRest gitRest;
	protected HttpServletRequest request;

	@Override
	@Before
	public void setUp() {
		super.setUp();
		gitRest = new GitRest();
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		request = new MockHttpServletRequest();
		request.setAttribute("user", user);
	}

	@Test
	public void testRequestNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				gitRest.getDiffForJiraIssue(null, null).getStatus());
	}

	@Test
	public void testIssueKeyNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				gitRest.getDiffForJiraIssue(request, null).getStatus());
	}

	@Test
	public void testEmptyIssueKey() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				gitRest.getDiffForJiraIssue(request, "").getStatus());
	}

	@Test
	public void testUnknownIssueKey() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				gitRest.getDiffForJiraIssue(request, "HOUDINI-1").getStatus());
	}

	@Test
	public void testGitExtractionDisabled() {
		GitConfiguration gitConfig = ConfigPersistenceManager.getGitConfiguration("TEST");
		gitConfig.setActivated(false);
		ConfigPersistenceManager.saveGitConfiguration("TEST", gitConfig);
		assertEquals(Status.SERVICE_UNAVAILABLE.getStatusCode(),
				gitRest.getDiffForJiraIssue(request, "TEST-2").getStatus());
	}

	@Test
	@NonTransactional
	public void testExistingIssueKey() {
		GitConfiguration gitConfig = ConfigPersistenceManager.getGitConfiguration("TEST");
		gitConfig.setActivated(true);
		ConfigPersistenceManager.saveGitConfiguration("TEST", gitConfig);
		assertEquals(Status.OK.getStatusCode(),
				gitRest.getDiffForJiraIssue(request, "TEST-2").getStatus());
	}
}
