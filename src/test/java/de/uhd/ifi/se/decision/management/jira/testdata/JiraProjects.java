package de.uhd.ifi.se.decision.management.jira.testdata;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.MockProject;
import com.atlassian.jira.project.Project;

/**
 * Enum for the JIRA projects used in the unit tests. There is only one project
 * called "TEST".
 */
public enum JiraProjects {
	TEST;

	public Project createJiraProject(int id) {
		Project project = new MockProject(id, this.name());
		((MockProject) project).setKey(this.name());
		return project;
	}

	public Project getJiraProject() {
		return ComponentAccessor.getProjectManager().getProjectByCurrentKey(this.name());
	}

	public static Project getTestProject() {
		return TEST.getJiraProject();
	}
}
