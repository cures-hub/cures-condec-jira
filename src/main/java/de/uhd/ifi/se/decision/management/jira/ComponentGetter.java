package de.uhd.ifi.se.decision.management.jira;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.inject.Named;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.bc.project.ProjectService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserManager;

/**
 * @description Provides access to JIRA components. Automatically initialized.
 *              Similar to the ComponentAccessor that comes with JIRA.
 */
@Named("ComponentUtil")
public class ComponentGetter {
	private static final String PLUGIN_STORAGE_KEY = "de.uhd.ifi.se.decision.management.jira";

	@ComponentImport
	private static IssueService issueService;
	@ComponentImport
	private static ProjectService projectService;
	@ComponentImport
	private static UserManager userManager;
	@ComponentImport
	private static ActiveObjects activeObjects;

	@Inject
	public ComponentGetter(IssueService issueService, ProjectService projectService, UserManager userManager,
			ActiveObjects activeObjects) {
		setIssueService(issueService);
		setProjectService(projectService);
		setUserManager(userManager);
		setActiveObjects(activeObjects);
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

	public static UserManager getUserManager() {
		return userManager;
	}

	public static void setUserManager(UserManager userManager) {
		ComponentGetter.userManager = userManager;
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