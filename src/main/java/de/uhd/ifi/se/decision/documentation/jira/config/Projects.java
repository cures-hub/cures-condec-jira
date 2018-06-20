package de.uhd.ifi.se.decision.documentation.jira.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;

import de.uhd.ifi.se.decision.documentation.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.documentation.jira.model.DecisionKnowledgeProjectImpl;

public class Projects {

	public static Map<String, DecisionKnowledgeProject> getProjectsMap() {
		Map<String, DecisionKnowledgeProject> configMap = new ConcurrentHashMap<String, DecisionKnowledgeProject>();
		for (Project project : ComponentAccessor.getProjectManager().getProjects()) {
			String projectKey = project.getKey();
			String projectName = project.getName();
			DecisionKnowledgeProject jiraProject = new DecisionKnowledgeProjectImpl(projectKey, projectName);
			configMap.put(projectKey, jiraProject);
		}
		return configMap;
	}
}
