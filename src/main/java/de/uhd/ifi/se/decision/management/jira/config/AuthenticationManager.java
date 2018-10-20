package de.uhd.ifi.se.decision.management.jira.config;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.sal.api.user.UserManager;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;

public class AuthenticationManager {

	protected static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationManager.class);

	public static boolean isProjectAdmin(String username, String projectKey) {
		if (username == null || projectKey == null) {
			LOGGER.error("Username or project key are null.");
			return false;
		}
		ApplicationUser user = getUser(username);
		Project project = ComponentAccessor.getProjectManager().getProjectByCurrentKey(projectKey);

		ProjectRoleManager projectRoleManager = ComponentAccessor.getComponent(ProjectRoleManager.class);
		Collection<ProjectRole> roles = projectRoleManager.getProjectRoles(user, project);
		if (roles == null) {
			LOGGER.error("User roles are not set correctly.");
			return false;
		}
		for (ProjectRole role : roles) {
			if (role.getName().equalsIgnoreCase("Administrators")) {
				return true;
			}
		}
		return false;
	}
	
	public static ApplicationUser getUser(String username) {
		return ComponentAccessor.getUserManager().getUserByName(username);
	}

	public static String getUsername(HttpServletRequest request) {
		UserManager userManager = ComponentGetter.getUserManager();
		return userManager.getRemoteUsername(request);
	}

	public static ApplicationUser getUser(HttpServletRequest request) {
		String username = getUsername(request);
		return getUser(username);
	}
}
