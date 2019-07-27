package de.uhd.ifi.se.decision.management.jira.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.mock.issue.MockIssue;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraProjects;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestJiraIssueTabPanelRenderer extends TestSetUpWithIssues {

	private JiraIssueTabPanelRenderer renderer;

	@Before
	public void setUp() {
		renderer = new JiraIssueTabPanelRenderer();
		initialization();
	}

	@Test
	public void testGetActionsNullNull() {
		assertEquals(0, renderer.getActions(null, null).size(), 0.0);
	}

	@Test
	public void testGetActionsFilledNull() {
		Issue issue = new MockIssue();
		assertEquals(0, renderer.getActions(issue, null).size(), 0.0);
	}

	@Test
	public void testGetActionsNullFilled() {
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		assertEquals(0, renderer.getActions(null, user).size(), 0.0);
	}

	@Test
	// TODO Is this the correct behaviour/name?
	public void testGetActionsFilledFilledTemplateNotProvided() {
		Project project = JiraProjects.getTestProject();
		Issue issue = new MockIssue();
		((MockIssue) issue).setProjectObject(project);
		((MockIssue) issue).setKey("TEST-1");
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		assertEquals(1, renderer.getActions(issue, user).size());
	}

	@Test
	public void testShowPanelNullNull() {
		assertFalse(renderer.showPanel(null, null));
	}

	@Test
	public void testShowPanelFilleNull() {
		Issue issue = new MockIssue();
		assertFalse(renderer.showPanel(issue, null));
	}

	@Test
	public void testShowPanelNullFilled() {
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		assertFalse(renderer.showPanel(null, user));
	}

	@Test
	@NonTransactional
	public void testShowPanelFilledFilled() {
		Project project = JiraProjects.getTestProject();
		Issue issue = new MockIssue();
		((MockIssue) issue).setProjectObject(project);
		((MockIssue) issue).setKey("TEST-1");
		((MockIssue) issue).setId((long) 1337);
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		assertTrue(renderer.showPanel(issue, user));
	}

	@Test
	@NonTransactional
	public void testGetActionsFilledFilledWithoutComment() {
		Project project = JiraProjects.getTestProject();
		Issue issue = new MockIssue();
		((MockIssue) issue).setProjectObject(project);
		((MockIssue) issue).setKey("TEST-1");
		((MockIssue) issue).setId((long) 1337);
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		assertNotNull(renderer.getActions(issue, user));
	}

}
