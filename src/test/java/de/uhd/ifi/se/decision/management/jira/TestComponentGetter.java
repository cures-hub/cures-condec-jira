package de.uhd.ifi.se.decision.management.jira;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.bc.project.ProjectService;
import com.atlassian.sal.api.user.UserManager;

import de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager;

public class TestComponentGetter extends TestSetUpWithIssues {

	private IssueService issueService;
	private ProjectService projectService;
	private UserManager userManager;
	private ActiveObjects activeObjects;

	@Before
	public void setUp() {
		initialization();
		issueService = mock(IssueService.class);
		projectService = mock(ProjectService.class);
		userManager = new MockUserManager();
		activeObjects = mock(ActiveObjects.class);

		new ComponentGetter(issueService, projectService, userManager, activeObjects);
	}

	@Test
	public void testGetIssueService() {
		assertEquals(issueService, ComponentGetter.getIssueService());
	}

	@Test
	public void testGetProjectService() {
		assertEquals(projectService, ComponentGetter.getProjectService());
	}

	@Test
	public void testGetUserManager() {
		assertEquals(userManager, ComponentGetter.getUserManager());
	}

	@Test
	public void testGetActivObjects() {
		assertEquals(activeObjects, ComponentGetter.getActiveObjects());
	}

	@Test
	public void testGetPluginStorageKey() {
		assertEquals("de.uhd.ifi.se.decision.management.jira", ComponentGetter.getPluginStorageKey());
	}

	@Test
	public void testSetActiveObjects() {
		ActiveObjects activeObjects = mock(ActiveObjects.class);
		ComponentGetter.setActiveObjects(activeObjects);
		assertEquals(activeObjects, ComponentGetter.getActiveObjects());
	}

	@Test
	public void testGetUrlOfImageFolder() {
		assertEquals("null/download/resources/de.uhd.ifi.se.decision.management.jira:stylesheet-and-icon-resources/",
				ComponentGetter.getUrlOfImageFolder());
	}

	@Test
	public void testGetUrlOfClassifierFolder() {
		assertEquals("null/download/resources/de.uhd.ifi.se.decision.management.jira:classifier-resources/",
				ComponentGetter.getUrlOfClassifierFolder());
	}
}
