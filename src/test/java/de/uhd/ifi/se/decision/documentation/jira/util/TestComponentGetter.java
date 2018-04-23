package de.uhd.ifi.se.decision.documentation.jira.util;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.bc.project.ProjectService;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;

import de.uhd.ifi.se.decision.documentation.jira.ComponentGetter;
import de.uhd.ifi.se.decision.documentation.jira.mocks.MockDefaultUserManager;


public class TestComponentGetter {

	private ComponentGetter getter;

	private PluginSettingsFactory factory;
	private TransactionTemplate transactionTemplate;
	private IssueService issueService;
	private ProjectService projectService;
    private SearchService searchService;
    private UserManager userManager;
    private TemplateRenderer templateRenderer;
    private ActiveObjects ao;

	@Before
	public void setUp() {
		factory = mock(PluginSettingsFactory.class);
		transactionTemplate = mock(TransactionTemplate.class);
		issueService = mock(IssueService.class);
		projectService = mock(ProjectService.class);
		searchService= mock(SearchService.class);
		userManager = new MockDefaultUserManager();
		templateRenderer = mock(TemplateRenderer.class);
		ao = mock(ActiveObjects.class);

		getter= new ComponentGetter(factory, transactionTemplate,
				issueService, projectService, searchService, userManager, templateRenderer, ao);
	}

	@Test
	public void testgetPluginSettingsFactory() {
		assertEquals(factory, ComponentGetter.getPluginSettingsFactory());
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
		assertEquals(ao, ComponentGetter.getActiveObjects());
	}

	@Test
	public void testGetPluginStorageKey() {
		assertEquals("de.uhd.ifi.se.decision.documentation.jira",ComponentGetter.getPluginStorageKey());
	}

	@Test
	public void testSetAO() {
		ActiveObjects newao = mock(ActiveObjects.class);
		ComponentGetter.setActiveObjects(newao);
		assertEquals(newao, ComponentGetter.getActiveObjects());
	}
}
