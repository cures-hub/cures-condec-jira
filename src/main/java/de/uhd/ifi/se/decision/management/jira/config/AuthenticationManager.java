package de.uhd.ifi.se.decision.management.jira.config;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.HashSet;

/**
 * Provides methods to handle user authentication to the administration pages
 * and for knowledge management
 */
public class AuthenticationManager {

	protected static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationManager.class);

	public static boolean isProjectAdmin(HttpServletRequest request) {
		String projectKey = request.getParameter("projectKey");
		String username = getUsername(request);
		return AuthenticationManager.isProjectAdmin(username, projectKey);
	}

	public static UserProfile getUserProfile(HttpServletRequest request) {
		UserManager userManager = ComponentGetter.getUserManager();
		UserProfile userProfile = userManager.getRemoteUser(request);
		return userProfile;
	}

	public static String getUsername(HttpServletRequest request) {
		return getUserProfile(request).getUsername();
	}

	public static boolean isProjectAdmin(String username, String projectKey) {
		if (username == null || projectKey == null) {
			LOGGER.error("Username or project key are null.");
			return false;
		}
		ApplicationUser user = getUser(username);
		if (user == null) {
			return false;
		}
		Collection<ProjectRole> roles = getRolesInProject(projectKey, user);
		for (ProjectRole role : roles) {
			if (role.getName().equalsIgnoreCase("Administrators")) {
				return true;
			}
		}
		return false;
	}

	public static Collection<ProjectRole> getRolesInProject(String projectKey, String username) {
		ApplicationUser user = getUser(username);
		return getRolesInProject(projectKey, user);
	}

	public static Collection<ProjectRole> getRolesInProject(String projectKey, ApplicationUser user) {
		Project project = ComponentAccessor.getProjectManager().getProjectByCurrentKey(projectKey);
		ProjectRoleManager projectRoleManager = ComponentAccessor.getComponent(ProjectRoleManager.class);
		Collection<ProjectRole> roles = projectRoleManager.getProjectRoles(user, project);
		if (roles == null) {
			LOGGER.error("User roles could not be retrieved.");
			return new HashSet<ProjectRole>();
		}
		return roles;
	}

	public static boolean isSystemAdmin(HttpServletRequest request) {
		UserProfile userProfile = getUserProfile(request);
		return ComponentGetter.getUserManager().isSystemAdmin(userProfile.getUserKey());
	}

	public static ApplicationUser getUser(String username) {
		return ComponentAccessor.getUserManager().getUserByName(username);
	}

	public static ApplicationUser getUser(HttpServletRequest request) {
		String username = getUsername(request);
		return getUser(username);
	}
}
