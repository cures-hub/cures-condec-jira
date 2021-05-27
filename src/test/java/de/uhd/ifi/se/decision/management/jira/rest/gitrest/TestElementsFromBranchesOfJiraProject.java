package de.uhd.ifi.se.decision.management.jira.rest.gitrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.GitConfiguration;
import de.uhd.ifi.se.decision.management.jira.mocks.MockPluginSettings;
import de.uhd.ifi.se.decision.management.jira.mocks.MockPluginSettingsFactory;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.rest.GitRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestElementsFromBranchesOfJiraProject extends TestSetUpGit {
	private GitRest gitRest;
	protected HttpServletRequest request;

	@Override
	@Before
	public void setUp() {
		gitRest = new GitRest();
		init();
		ApplicationUser user = JiraUsers.BLACK_HEAD.getApplicationUser();
		request = new MockHttpServletRequest();
		request.setAttribute("user", user);
	}

	@Test
	public void testEmptyIssueKey() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), gitRest.getElementsFromAllBranchesOfProject("").getStatus());
	}

	@Test
	public void testUnknownProjectKey() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				gitRest.getElementsFromAllBranchesOfProject("HOUDINI").getStatus());
	}

	@Test
	public void testExistingProjectKey() {
		GitConfiguration gitConfig = ConfigPersistenceManager.getGitConfiguration("TEST");
		gitConfig.setActivated(true);
		ConfigPersistenceManager.saveGitConfiguration("TEST", gitConfig);
		assertEquals(Status.OK.getStatusCode(), gitRest.getElementsFromAllBranchesOfProject("TEST").getStatus());
	}

	@Test
	public void testGitExtractionDisabled() {
		GitConfiguration gitConfig = ConfigPersistenceManager.getGitConfiguration("TEST");
		gitConfig.setActivated(false);
		ConfigPersistenceManager.saveGitConfiguration("TEST", gitConfig);
		assertEquals(Status.SERVICE_UNAVAILABLE.getStatusCode(),
				gitRest.getElementsFromAllBranchesOfProject("TEST").getStatus());
	}

	@After
	public void tearDown() {
		// reset plugin settings to default settings
		MockPluginSettingsFactory.pluginSettings = new MockPluginSettings();
	}
}
