package de.uhd.ifi.se.decision.management.jira;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Named;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.config.util.JiraHome;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserManager;

import de.uhd.ifi.se.decision.management.jira.config.AuthenticationManager;
import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;

/**
 * Provides access to Jira components. Automatically initialized. The
 * ComponentGetter is similar to the ComponentAccessor that comes with Jira, but
 * is provided by the ConDec plugin. It enables to access the active objects
 * databases for object relational mapping. Further, it contains a different
 * user manager than that provided by the {@link ComponentAccessor} to handle
 * users in HTTP requests.
 * 
 * @see AuthenticationManager
 * @see KnowledgePersistenceManager
 */
@Named("ComponentUtil")
public class ComponentGetter {

	public static final String PLUGIN_KEY = "de.uhd.ifi.se.decision.management.jira";
	public static final String PLUGIN_HOME = ComponentAccessor.getComponentOfType(JiraHome.class).getDataDirectory()
			.getAbsolutePath() + File.separator + "condec-plugin" + File.separator;

	@ComponentImport
	private static UserManager userManager;
	@ComponentImport
	private static ActiveObjects activeObjects;

	@Inject
	public ComponentGetter(UserManager userManager, ActiveObjects activeObjects) {
		setUserManager(userManager);
		setActiveObjects(activeObjects);
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

	private static String getUrlOfResourcesFolder() {
		ApplicationProperties applicationProperties = ComponentAccessor.getApplicationProperties();
		return applicationProperties.getString(APKeys.JIRA_BASEURL) + "/download/resources/" + PLUGIN_KEY;
	}

	public static String getUrlOfImageFolder() {
		return getUrlOfResourcesFolder() + ":stylesheet-and-icon-resources/";
	}

	public static String getUrlOfClassifierFolder() {
		return getUrlOfResourcesFolder() + ":classifier-resources/";
	}

	/**
	 * Removes the singleton objects of the {@link KnowledgeGraph}, the
	 * {@link KnowledgePersistenceManager}, and the {@link GitClient} for a project.
	 * 
	 * @param projectKey
	 *            of the Jira project.
	 */
	public static void removeInstances(String projectKey) {
		KnowledgeGraph.instances.remove(projectKey);
		KnowledgePersistenceManager.instances.remove(projectKey);
		GitClient.instances.remove(projectKey);
	}
}