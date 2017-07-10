package com.atlassian.DecisionDocumentation.util;

import javax.inject.Inject;
import javax.inject.Named;

import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.bc.project.ProjectService;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 
 * @author Ewald Rode
 * @description
 */
@Named("ComponentUtil")
@Scanned
public class ComponentGetter {

    private static final Logger LOGGER = LoggerFactory.getLogger("com.atlassian.snikjira.snikjira");
    private static final String PLUGIN_STORAGE_KEY = "JIRADecDoc.ProjectKey.";
    
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
    
    @Inject
    public ComponentGetter(PluginSettingsFactory pluginSettingsFactory, TransactionTemplate transactionTemplate, 
    		IssueService issueService, ProjectService projectService, 
            SearchService searchService, UserManager userManager,
            TemplateRenderer templateRenderer) {
        ComponentGetter.pluginSettingsFactory = pluginSettingsFactory;
        ComponentGetter.transactionTemplate = transactionTemplate;
        ComponentGetter.issueService = issueService;
        ComponentGetter.projectService = projectService;
        ComponentGetter.searchService = searchService;
        ComponentGetter.userManager = userManager;
        ComponentGetter.templateRenderer = templateRenderer;
    }

    public static PluginSettingsFactory getPluginSettingsFactory() {
        return pluginSettingsFactory;
    }

    public static TransactionTemplate getTransactionTemplate() {
        return transactionTemplate;
    }

    public static IssueService getIssueService() {
    	return issueService;
    }
    
    public static ProjectService getProjectService() {
    	return projectService;
    }
    
    public static SearchService getSearchService() {
    	return searchService;
    }
    
    public static UserManager getUserManager() {
    	return userManager;
    }
    
    public static TemplateRenderer getTemplateRenderer() {
    	return templateRenderer;
    }
    
    public static String getPluginStorageKey() {
        return PLUGIN_STORAGE_KEY;
    }
}