package de.uhd.ifi.se.decision.documentation.jira.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;

import de.uhd.ifi.se.decision.documentation.jira.model.DecisionKnowledgeProject;

public class Projects {

	List<DecisionKnowledgeProject> projects;

	public Projects() {
		this.projects = new ArrayList<DecisionKnowledgeProject>();
	}

	public static Map<String, DecisionKnowledgeProject> getProjectsMap() {
		Map<String, DecisionKnowledgeProject> configMap = new HashMap<String, DecisionKnowledgeProject>();
		for (Project project : ComponentAccessor.getProjectManager().getProjects()) {
			String projectKey = project.getKey();
			String projectName = project.getName();
			DecisionKnowledgeProject jiraProject = new DecisionKnowledgeProjectImpl(projectKey, projectName);
			configMap.put(projectKey, jiraProject);
		}
		return configMap;
	}
}
