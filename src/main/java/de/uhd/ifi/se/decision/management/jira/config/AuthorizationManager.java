package de.uhd.ifi.se.decision.management.jira.config;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.atlassian.jira.user.ApplicationUser;

public class AuthorizationManager {

	protected static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationManager.class);

	public static boolean isProjectAdmin(String username, String projectKey) {
		if (username == null || projectKey == null) {
			LOGGER.error("Username or project key are null.");
			return false;
		}
		ApplicationUser user = ComponentAccessor.getUserManager().getUserByName(username);
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
}
