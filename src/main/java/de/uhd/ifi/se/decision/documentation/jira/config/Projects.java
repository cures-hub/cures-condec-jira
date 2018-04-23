package de.uhd.ifi.se.decision.documentation.jira.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;

import de.uhd.ifi.se.decision.documentation.jira.model.IProject;

public class Projects {

	List<IProject> projects;

	public Projects() {
		this.projects = new ArrayList<IProject>();
	}

	public static Map<String, IProject> getProjectsMap() {
		Map<String, IProject> configMap = new HashMap<String, IProject>();
		for (Project project : ComponentAccessor.getProjectManager().getProjects()) {
			String projectKey = project.getKey();
			String projectName = project.getName();
			IProject jiraProject = new JiraProject(projectKey, projectName);
			configMap.put(projectKey, jiraProject);
		}
		return configMap;
	}
}
