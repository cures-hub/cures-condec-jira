package de.uhd.ifi.se.decision.management.jira;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Named;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserManager;

import de.uhd.ifi.se.decision.management.jira.config.AuthenticationManager;
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

	private static String getUrlOfResourceFolder() {
		ApplicationProperties applicationProperties = ComponentAccessor.getApplicationProperties();
		return applicationProperties.getString(APKeys.JIRA_BASEURL) + File.separator + "download" + File.separator
				+ "resources" + File.separator + PLUGIN_KEY;
	}

	public static String getUrlOfImageFolder() {
		return getUrlOfResourceFolder() + ":stylesheet-and-icon-resources" + File.separator;
	}

	public static String getUrlOfClassifierFolder() {
		return getUrlOfResourceFolder() + ":classifier-resources" + File.separator;
	}
}