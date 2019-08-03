package de.uhd.ifi.se.decision.management.jira;

import static com.google.common.base.Preconditions.checkNotNull;

import de.uhd.ifi.se.decision.management.jira.extraction.GitExtractor;

import javax.inject.Inject;
import javax.inject.Named;

import java.util.HashMap;
import java.util.Map;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.bc.project.ProjectService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import org.eclipse.jgit.api.Git;

/**
 * @description Provides access to JIRA components. Automatically initialized.
 */
@Named("ComponentUtil")
public class ComponentGetter {
	private static final String PLUGIN_STORAGE_KEY = "de.uhd.ifi.se.decision.management.jira";

	@ComponentImport
	private static PluginSettingsFactory pluginSettingsFactory;
	@ComponentImport
	private static TransactionTemplate transactionTemplate;
	@ComponentImport
	private static IssueService issueService;
	@ComponentImport
	private static ProjectService projectService;
	@ComponentImport
	private static SearchService searchService;
	@ComponentImport
	private static UserManager userManager;
	@ComponentImport
	private static TemplateRenderer templateRenderer;
	@ComponentImport
	private static ActiveObjects activeObjects;

	/* ConDec git extraction manager, one per Jira Project */

	/**
	 * @issue How to access knowledge extracted from commits?
	 * @decision Hold cached commits and their knowledge in globaly accessible class.
	 * @pro consistency: git repositories are another knowledge source like activeObjects or issueService
	 * @con resources: consumes more memory, risk of resource starving with poor implementation
	 * @alternative instantiate classes for commit knowledge extraction only when needed.
	 * @con performance: IO access is costly and requires more CPU time 
	 * @pro resources: consumes less memory, CPU time costs are low
	 */
	private static Map<String, GitExtractor> gitExtractors = null; // projecShortName as the key

	@Inject
	public ComponentGetter(PluginSettingsFactory pluginSettingsFactory, TransactionTemplate transactionTemplate,
			IssueService issueService, ProjectService projectService, SearchService searchService,
			UserManager userManager, TemplateRenderer templateRenderer, ActiveObjects activeObjects) {
		setPluginSettingsFactory(pluginSettingsFactory);
		setTransactionTemplate(transactionTemplate);
		setIssueService(issueService);
		setProjectService(projectService);
		setSearchService(searchService);
		setUserManager(userManager);
		setTemplateRenderer(templateRenderer);
		setActiveObjects(activeObjects);
	}

	public static PluginSettingsFactory getPluginSettingsFactory() {
		return pluginSettingsFactory;
	}

	public static void setPluginSettingsFactory(PluginSettingsFactory pluginSettingsFactory) {
		ComponentGetter.pluginSettingsFactory = pluginSettingsFactory;
	}

	public static TransactionTemplate getTransactionTemplate() {
		return transactionTemplate;
	}

	public static void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		ComponentGetter.transactionTemplate = transactionTemplate;
	}

	public static IssueService getIssueService() {
		return issueService;
	}

	public static void setIssueService(IssueService issueService) {
		ComponentGetter.issueService = issueService;
	}

	public static ProjectService getProjectService() {
		return projectService;
	}

	public static void setProjectService(ProjectService projectService) {
		ComponentGetter.projectService = projectService;
	}

	public static SearchService getSearchService() {
		return searchService;
	}

	public static void setSearchService(SearchService searchService) {
		ComponentGetter.searchService = searchService;
	}

	public static UserManager getUserManager() {
		return userManager;
	}

	public static void setUserManager(UserManager userManager) {
		ComponentGetter.userManager = userManager;
	}

	public static TemplateRenderer getTemplateRenderer() {
		return templateRenderer;
	}

	public static void setTemplateRenderer(TemplateRenderer templateRenderer) {
		ComponentGetter.templateRenderer = templateRenderer;
	}

	public static ActiveObjects getActiveObjects() {
		return activeObjects;
	}

	public static void setActiveObjects(ActiveObjects activeObjects) {
		ComponentGetter.activeObjects = checkNotNull(activeObjects);
	}

	public static String getPluginStorageKey() {
		return PLUGIN_STORAGE_KEY;
	}

	public static String getUrlOfImageFolder() {
		ApplicationProperties applicationProperties = ComponentAccessor.getApplicationProperties();
		return applicationProperties.getString(APKeys.JIRA_BASEURL) + "/download/resources/"
				+ ComponentGetter.getPluginStorageKey() + ":stylesheet-and-icon-resources/";
	}

	public static String getUrlOfClassifierFolder() {
		ApplicationProperties applicationProperties = ComponentAccessor.getApplicationProperties();
		return applicationProperties.getString(APKeys.JIRA_BASEURL) + "/download/resources/"
				+ ComponentGetter.getPluginStorageKey() + ":classifier-resources/";
	}

	/* TODO: make sure GitExtractor can be accessed only through this method somehow... */
	public static GitExtractor getGitExtractor(String projectKey){
		if (gitExtractors==null) {
			gitExtractors = new HashMap<String, GitExtractor>();
		}

		if (!gitExtractors.containsKey(projectKey)) {
			System.err.println("getGitExtractor for project:"+projectKey);
			try {
				GitExtractor gE = new GitExtractor(projectKey);
				// TODO: also check gE status
				if (gE != null) {
					gitExtractors.put(projectKey, gE);
					System.err.println("Set GitExtractor for project:" + projectKey);
				} else {
					System.err.println("COULD not set GitExtractor for project:" + projectKey);
					return null;
				}
			}
			catch (Exception ex) {
				System.err.println("getGitExtractor exception:"+ex.getMessage());
			}
		}
		System.err.println("Retrieved GitExtractor for project:"+projectKey);
		return gitExtractors.get(projectKey);
	}
}