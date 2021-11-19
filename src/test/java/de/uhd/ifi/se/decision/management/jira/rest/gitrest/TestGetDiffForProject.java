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

public class TestGetDiffForProject extends TestSetUpGit {
	private GitRest gitRest;
	protected HttpServletRequest request;

	@Override
	@Before
	public void setUp() {
		gitRest = new GitRest();
		super.setUp();
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		request = new MockHttpServletRequest();
		request.setAttribute("user", user);
	}

	@Test
	public void testEmptyIssueKey() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), gitRest.getDiffForProject("").getStatus());
	}

	@Test
	public void testUnknownProjectKey() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				gitRest.getDiffForProject("HOUDINI").getStatus());
	}

	@Test
	public void testExistingProjectKey() {
		GitConfiguration gitConfig = ConfigPersistenceManager.getGitConfiguration("TEST");
		gitConfig.setActivated(true);
		ConfigPersistenceManager.saveGitConfiguration("TEST", gitConfig);
		assertEquals(Status.OK.getStatusCode(), gitRest.getDiffForProject("TEST").getStatus());
	}

	@Test
	public void testGitExtractionDisabled() {
		GitConfiguration gitConfig = ConfigPersistenceManager.getGitConfiguration("TEST");
		gitConfig.setActivated(false);
		ConfigPersistenceManager.saveGitConfiguration("TEST", gitConfig);
		assertEquals(Status.SERVICE_UNAVAILABLE.getStatusCode(),
				gitRest.getDiffForProject("TEST").getStatus());
	}
}
