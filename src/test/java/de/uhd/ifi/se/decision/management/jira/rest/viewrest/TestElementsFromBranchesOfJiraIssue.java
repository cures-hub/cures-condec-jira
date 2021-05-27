package de.uhd.ifi.se.decision.management.jira.rest.viewrest;

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
import de.uhd.ifi.se.decision.management.jira.rest.ViewRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

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
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				viewRest.getElementsOfFeatureBranchForJiraIssue(null, null).getStatus());
	}

	@Test
	public void testIssueKeyNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				viewRest.getElementsOfFeatureBranchForJiraIssue(request, null).getStatus());
	}

	@Test
	public void testEmptyIssueKey() {
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
		GitConfiguration gitConfig = ConfigPersistenceManager.getGitConfiguration("TEST");
		gitConfig.setActivated(false);
		ConfigPersistenceManager.saveGitConfiguration("TEST", gitConfig);
		assertEquals(Status.SERVICE_UNAVAILABLE.getStatusCode(),
				viewRest.getElementsOfFeatureBranchForJiraIssue(request, "TEST-2").getStatus());
	}

	@Test
	@NonTransactional
	public void testExistingIssueKey() {
		assertEquals(Status.OK.getStatusCode(),
				viewRest.getElementsOfFeatureBranchForJiraIssue(request, "TEST-2").getStatus());
	}

	@After
	public void tearDown() {
		// reset plugin settings to default settings
		MockPluginSettingsFactory.pluginSettings = new MockPluginSettings();
	}
}
