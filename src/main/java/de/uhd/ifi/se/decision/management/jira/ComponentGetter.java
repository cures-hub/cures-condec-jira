package de.uhd.ifi.se.decision.management.jira;

import javax.inject.Inject;
import javax.inject.Named;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.bc.project.ProjectService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;

import static com.google.common.base.Preconditions.*;

/**
 * @description Provides access to JIRA components. Automatically initialized.
 */
@Named("ComponentUtil")
@Scanned
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
}