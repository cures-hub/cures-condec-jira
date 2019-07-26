package de.uhd.ifi.se.decision.management.jira.mocks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.atlassian.jira.project.Project;
import com.atlassian.jira.security.roles.DefaultRoleActors;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.security.roles.ProjectRoleActors;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.atlassian.jira.user.ApplicationUser;

public class MockProjectRoleManager implements ProjectRoleManager {
	@Override
	public Collection<ProjectRole> getProjectRoles() {
		return null;
	}

	@Override
	public Collection<ProjectRole> getProjectRoles(ApplicationUser applicationUser, Project project) {
		if (applicationUser == null) {
			return null;
		}
		Collection<ProjectRole> roles = new ArrayList<>();
		if (applicationUser.getName().equals("SysAdmin")) {
			ProjectRole role = new MockProjectRole();
			((MockProjectRole) role).setId((long) 4);
			((MockProjectRole) role).setName("Administrators");
			((MockProjectRole) role).setDescription("Test");
			roles.add(role);
		}
		return roles;
	}

	@Override
	public ProjectRole getProjectRole(Long aLong) {
		return null;
	}

	@Override
	public ProjectRole getProjectRole(String s) {
		return null;
	}

	@Override
	public ProjectRole createRole(ProjectRole projectRole) {
		return null;
	}

	@Override
	public boolean isRoleNameUnique(String s) {
		return false;
	}

	@Override
	public void deleteRole(ProjectRole projectRole) {
		// method empty since not used for testing
	}

	@Override
	public void updateRole(ProjectRole projectRole) {
		// method empty since not used for testing
	}

	@Override
	public ProjectRoleActors getProjectRoleActors(ProjectRole projectRole, Project project) {
		return null;
	}

	@Override
	public void updateProjectRoleActors(ProjectRoleActors projectRoleActors) {
		// method empty since not used for testing
	}

	@Override
	public DefaultRoleActors getDefaultRoleActors(ProjectRole projectRole) {
		return null;
	}

	@Override
	public void updateDefaultRoleActors(DefaultRoleActors defaultRoleActors) {
		// method empty since not used for testing
	}

	@Override
	public void applyDefaultsRolesToProject(Project project) {
		// method empty since not used for testing
	}

	@Override
	public void removeAllRoleActorsByNameAndType(String s, String s1) {
		// method empty since not used for testing
	}

	@Override
	public void removeAllRoleActorsByProject(Project project) {
		// method empty since not used for testing
	}

	@Override
	public boolean isUserInProjectRole(ApplicationUser applicationUser, ProjectRole projectRole, Project project) {
		return false;
	}

	@Override
	public Collection<Long> getProjectIdsContainingRoleActorByNameAndType(String s, String s1) {
		return null;
	}

	@Override
	public List<Long> roleActorOfTypeExistsForProjects(List<Long> list, ProjectRole projectRole, String s, String s1) {
		return null;
	}

	@Override
	public Map<Long, List<String>> getProjectIdsForUserInGroupsBecauseOfRole(List<Long> list, ProjectRole projectRole,
			String s, String s1) {
		return null;
	}

	@Override
	public ProjectIdToProjectRoleIdsMap createProjectIdToProjectRolesMap(ApplicationUser applicationUser,
			Collection<Long> collection) {
		return null;
	}
}
