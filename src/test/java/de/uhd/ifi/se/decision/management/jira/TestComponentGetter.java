package de.uhd.ifi.se.decision.management.jira;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.bc.project.ProjectService;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;

import de.uhd.ifi.se.decision.management.jira.mocks.MockDefaultUserManager;
import de.uhd.ifi.se.decision.management.jira.mocks.MockPluginSettingsFactory;

public class TestComponentGetter {

	private PluginSettingsFactory pluginSettingsFactory;
	private TransactionTemplate transactionTemplate;
	private IssueService issueService;
	private ProjectService projectService;
	private SearchService searchService;
	private UserManager userManager;
	private TemplateRenderer templateRenderer;
	private ActiveObjects activeObjects;

	public static void init(ActiveObjects activeObjects, TransactionTemplate transactionTemplate, UserManager userManager) {
		new ComponentGetter(new MockPluginSettingsFactory(), transactionTemplate, null, null, null, userManager, null, activeObjects);
	}

	@Before
	public void setUp() {
		pluginSettingsFactory = mock(PluginSettingsFactory.class);
		transactionTemplate = mock(TransactionTemplate.class);
		issueService = mock(IssueService.class);
		projectService = mock(ProjectService.class);
		searchService = mock(SearchService.class);
		userManager = new MockDefaultUserManager();
		templateRenderer = mock(TemplateRenderer.class);
		activeObjects = mock(ActiveObjects.class);

		new ComponentGetter(pluginSettingsFactory, transactionTemplate, issueService, projectService, searchService,
				userManager, templateRenderer, activeObjects);
	}

	@Test
	public void testGetPluginSettingsFactory() {
		assertEquals(pluginSettingsFactory, ComponentGetter.getPluginSettingsFactory());
	}

	@Test
	public void testGetTransactionTemplate() {
		assertEquals(transactionTemplate, ComponentGetter.getTransactionTemplate());
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
	public void testGetSearchService() {
		assertEquals(searchService, ComponentGetter.getSearchService());
	}

	@Test
	public void testGetUserManager() {
		assertEquals(userManager, ComponentGetter.getUserManager());
	}

	@Test
	public void testGetTemplateRenderer() {
		assertEquals(templateRenderer, ComponentGetter.getTemplateRenderer());
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
}
