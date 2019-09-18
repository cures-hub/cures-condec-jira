package de.uhd.ifi.se.decision.management.jira;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserManager;

import de.uhd.ifi.se.decision.management.jira.config.AuthenticationManager;
import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.GitClientImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.ActiveObjectPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueTextPersistenceManager;

/**
 * Provides access to JIRA components. Automatically initialized. The
 * ComponentGetter is similar to the ComponentAccessor that comes with JIRA, but
 * is provided by the ConDec plugin. It enables to access the active objects
 * databases for object relational mapping. Further, it contains a different
 * user manager than that provided by the {@link ComponentAccessor} to handle
 * users in HTTP requests.
 * 
 * @see AuthenticationManager
 * @see JiraIssueTextPersistenceManager
 * @see ActiveObjectPersistenceManager
 */
@Named("ComponentUtil")
public class ComponentGetter {

	public final static String PLUGIN_KEY = "de.uhd.ifi.se.decision.management.jira";

	@ComponentImport
	private static UserManager userManager;
	@ComponentImport
	private static ActiveObjects activeObjects;

	/**
	 * @issue How to access knowledge extracted from commits?
	 * @decision Hold cached commits and their knowledge in a globally accessible
	 *           class!
	 * @pro consistency: git repositories are another knowledge source like
	 *      activeObjects or issueService
	 * @con resources: consumes more memory, risk of resource starving with poor
	 *      implementation
	 * @alternative Instantiate classes for commit knowledge extraction only when
	 *              needed!
	 * @con performance: IO access is costly and requires more CPU time
	 * @pro resources: consumes less memory, CPU time costs are low
	 */
	private static Map<String, GitClient> gitClients = new HashMap<String, GitClient>(); // projectKey

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

	public static String getUrlOfImageFolder() {
		ApplicationProperties applicationProperties = ComponentAccessor.getApplicationProperties();
		return applicationProperties.getString(APKeys.JIRA_BASEURL) + "/download/resources/" + PLUGIN_KEY
				+ ":stylesheet-and-icon-resources/";
	}

	public static String getUrlOfClassifierFolder() {
		ApplicationProperties applicationProperties = ComponentAccessor.getApplicationProperties();
		return applicationProperties.getString(APKeys.JIRA_BASEURL) + "/download/resources/" + PLUGIN_KEY
				+ ":classifier-resources/";
	}

	/**
	 * Provides the gitClient object associated with a project. TODO: We might add a
	 * GitExtractor class in the future, also to cope with more than one git repo
	 * for a project.
	 */
	public static GitClient getGitClient(String projectKey) {
		if (projectKey == null || projectKey.isEmpty()) {
			return null;
		}
		if (gitClients.containsKey(projectKey)) {
			return gitClients.get(projectKey);
		}
		GitClient gitClient = new GitClientImpl(projectKey);
		gitClients.put(projectKey, gitClient);
		return gitClient;
	}
}