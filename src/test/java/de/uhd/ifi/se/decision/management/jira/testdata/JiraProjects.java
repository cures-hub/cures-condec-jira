package de.uhd.ifi.se.decision.management.jira.testdata;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.MockProject;
import com.atlassian.jira.project.Project;

import de.uhd.ifi.se.decision.management.jira.mocks.MockComponentAccessor;

/**
 * Enum for the JIRA projects used in the unit tests. There is only one project
 * called "TEST". The test project is included in the
 * ComponentAccessor.getProjectManager().
 * 
 * @see MockComponentAccessor
 */
public enum JiraProjects {
	TEST, // test project commonly used during testing
	HTTP, GITHUB, GITLAB; // test projects for git authentication tests

	public Project createJiraProject(int id) {
		Project project = new MockProject(id, name());
		((MockProject) project).setKey(name());
		return project;
	}

	public Project getJiraProject() {
		return ComponentAccessor.getProjectManager().getProjectByCurrentKey(this.name());
	}

	public static Project getTestProject() {
		return TEST.getJiraProject();
	}
}
