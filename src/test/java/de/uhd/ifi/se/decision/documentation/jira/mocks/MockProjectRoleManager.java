package de.uhd.ifi.se.decision.documentation.jira.mocks;

import com.atlassian.jira.project.Project;
import com.atlassian.jira.security.roles.DefaultRoleActors;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.security.roles.ProjectRoleActors;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.atlassian.jira.user.ApplicationUser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class MockProjectRoleManager implements ProjectRoleManager {
    @Override
    public Collection<ProjectRole> getProjectRoles() {
        return null;
    }

    @Override
    public Collection<ProjectRole> getProjectRoles(ApplicationUser applicationUser, Project project) {
        Collection<ProjectRole> roles = new ArrayList<>();
        if(applicationUser.getName().equals("SysAdmin")){
            ProjectRole role = new MockProjectRole();
            ((MockProjectRole)role).setId((long) 4);
            ((MockProjectRole)role).setName("Administrators");
            ((MockProjectRole)role).setDescription("Test");
            roles.add(role);
        }
        if(applicationUser.getName().equals("NoSysAdmin")){
            return null;
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

    }

    @Override
    public void updateRole(ProjectRole projectRole) {

    }

    @Override
    public ProjectRoleActors getProjectRoleActors(ProjectRole projectRole, Project project) {
        return null;
    }

    @Override
    public void updateProjectRoleActors(ProjectRoleActors projectRoleActors) {

    }

    @Override
    public DefaultRoleActors getDefaultRoleActors(ProjectRole projectRole) {
        return null;
    }

    @Override
    public void updateDefaultRoleActors(DefaultRoleActors defaultRoleActors) {

    }

    @Override
    public void applyDefaultsRolesToProject(Project project) {

    }

    @Override
    public void removeAllRoleActorsByNameAndType(String s, String s1) {

    }

    @Override
    public void removeAllRoleActorsByProject(Project project) {

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
    public Map<Long, List<String>> getProjectIdsForUserInGroupsBecauseOfRole(List<Long> list, ProjectRole projectRole, String s, String s1) {
        return null;
    }

    @Override
    public ProjectIdToProjectRoleIdsMap createProjectIdToProjectRolesMap(ApplicationUser applicationUser, Collection<Long> collection) {
        return null;
    }
}
