package de.uhd.ifi.se.decision.management.jira;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.inject.Named;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserManager;

import de.uhd.ifi.se.decision.management.jira.config.AuthenticationManager;
import de.uhd.ifi.se.decision.management.jira.persistence.ActiveObjectPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueTextPersistenceManager;

import de.uhd.ifi.se.decision.management.jira.extraction.GitExtractor;
import org.eclipse.jgit.api.Git;

/**
 * @description Provides access to JIRA components. Automatically initialized.
 *              The ComponentGetter is similar to the ComponentAccessor that
 *              comes with JIRA, but is provided by the ConDec plugin. It
 *              enables to access the active objects databases for object
 *              relational mapping. Further, it contains a different user
 *              manager than that provided by the {@link ComponentAccessor} to
 *              handle users in HTTP requests.
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