package de.uhd.ifi.se.decision.documentation.jira.config;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;

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

import de.uhd.ifi.se.decision.documentation.jira.config.IsActivated;
import de.uhd.ifi.se.decision.documentation.jira.mocks.MockDefaultUserManager;
import de.uhd.ifi.se.decision.documentation.jira.util.ComponentGetter;


public class TestIsActivated {
	private IsActivated isActivated;
	
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
		
		ComponentGetter getter= new ComponentGetter(factory, transactionTemplate,
				issueService, projectService, searchService, userManager, templateRenderer, ao);
		
		Map<String , String> context = new HashMap<>();	
		isActivated = new IsActivated();
		isActivated.init(context);
	}
	
	@Test
	public void testShouldDisplayNull() {
		assertFalse(isActivated.shouldDisplay(null));
	}
	
	@Test
	public void testShouldDisplayEmpty() {
		Map<String , Object> context = new HashMap<>();	
		assertFalse(isActivated.shouldDisplay(context));
	}
	
	@Test
	public void testShsouldDisplayFilled() {
		Map<String , Object> context = new HashMap<>();	
		context.put("projectKey", "TEST");
		assertFalse(isActivated.shouldDisplay(context));
	}

}
