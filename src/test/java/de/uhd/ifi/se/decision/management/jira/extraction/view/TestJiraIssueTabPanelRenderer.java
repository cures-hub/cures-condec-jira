package de.uhd.ifi.se.decision.management.jira.extraction.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.mock.issue.MockIssue;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.MockApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager;
import de.uhd.ifi.se.decision.management.jira.view.JiraIssueTabPanelRenderer;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.NonTransactional;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TestSetUpWithIssues.AoSentenceTestDatabaseUpdater.class)
public class TestJiraIssueTabPanelRenderer extends TestSetUpWithIssues {

	private EntityManager entityManager;
	private JiraIssueTabPanelRenderer renderer;

	@Before
	public void setUp() {
		renderer = new JiraIssueTabPanelRenderer();
		initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockUserManager());
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
		ApplicationUser user = new MockApplicationUser("NoFails");
		assertEquals(0, renderer.getActions(null, user).size(), 0.0);
	}

	@Test
	// TODO Is this the correct behaviour/name?
	public void testGetActionsFilledFilledTemplateNotProvided() {
		Project project = ComponentAccessor.getProjectManager().getProjectByCurrentKey("TEST");
		Issue issue = new MockIssue();
		((MockIssue) issue).setProjectObject(project);
		((MockIssue) issue).setKey("TEST-1");
		ApplicationUser user = new MockApplicationUser("NoFails");
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
		ApplicationUser user = new MockApplicationUser("NoFails");
		assertFalse(renderer.showPanel(null, user));
	}

	@Test
	@NonTransactional
	public void testShowPanelFilledFilled() {
		Project project = ComponentAccessor.getProjectManager().getProjectByCurrentKey("TEST");
		Issue issue = new MockIssue();
		((MockIssue) issue).setProjectObject(project);
		((MockIssue) issue).setKey("TEST-1");
		((MockIssue) issue).setId((long) 1337);
		ApplicationUser user = new MockApplicationUser("NoFails");
		assertTrue(renderer.showPanel(issue, user));
	}

	@Test
	@NonTransactional
	public void testGetActionsFilledFilledWithoutComment() {
		Project project = ComponentAccessor.getProjectManager().getProjectByCurrentKey("TEST");
		Issue issue = new MockIssue();
		((MockIssue) issue).setProjectObject(project);
		((MockIssue) issue).setKey("TEST-1");
		((MockIssue) issue).setId((long) 1337);
		ApplicationUser user = new MockApplicationUser("NoFails");
		assertNotNull(renderer.getActions(issue, user));
	}

}
