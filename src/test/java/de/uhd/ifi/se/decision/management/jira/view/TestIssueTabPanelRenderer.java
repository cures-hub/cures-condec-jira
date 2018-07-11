package de.uhd.ifi.se.decision.management.jira.view;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.mock.issue.MockIssue;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.MockApplicationUser;
import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.mocks.MockDefaultUserManager;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestIssueTabPanelRenderer extends TestSetUp {

	private EntityManager entityManager;
	private IssueTabPanelRenderer renderer;

	@Before
	public void setUp() {
		renderer = new IssueTabPanelRenderer();
		initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockDefaultUserManager());
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
	public void testGetActionsFilledFilled() {
		Issue issue = new MockIssue();
		ApplicationUser user = new MockApplicationUser("NoFails");
		assertEquals(1, renderer.getActions(issue, user).size(), 0.0);
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
	public void testShowPanelFilledFilled() {
		Project project = ComponentAccessor.getProjectManager().getProjectByCurrentKey("TEST");
		Issue issue = new MockIssue();
		((MockIssue) issue).setProjectObject(project);
		((MockIssue) issue).setKey("TEST-1");
		ApplicationUser user = new MockApplicationUser("NoFails");
		// Tab panel is currently disabled
		//assertFalse(renderer.showPanel(issue, user));
	}
}
