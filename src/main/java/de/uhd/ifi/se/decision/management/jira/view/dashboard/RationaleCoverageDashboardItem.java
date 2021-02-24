package de.uhd.ifi.se.decision.management.jira.view.dashboard;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.atlassian.jira.project.Project;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;

public class RationaleCoverageDashboardItem extends ConDecDashboardItem {

	@Override
	public Map<String, Object> getAdditionalParameters() {
		Map<String, Object> additionalParameters = new LinkedHashMap<>();

		List<Project> projects = DecisionKnowledgeProject.getProjectsWithConDecActivatedAndAccessableForUser(user);
		additionalParameters.put("projects", projects);

		List<Project> accessableProjectsWithGitRepo = new ArrayList<>();
		for (Project project : projects) {
			String projectKey = project.getKey();
			if (ConfigPersistenceManager.isKnowledgeExtractedFromGit(projectKey)) {
				accessableProjectsWithGitRepo.add(project);
			}
		}

		additionalParameters.put("projectsWithGit", accessableProjectsWithGitRepo);

		return additionalParameters;
	}
}