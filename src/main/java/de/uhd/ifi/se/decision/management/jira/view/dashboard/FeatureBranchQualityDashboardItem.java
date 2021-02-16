package de.uhd.ifi.se.decision.management.jira.view.dashboard;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.atlassian.jira.project.Project;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;

public class FeatureBranchQualityDashboardItem extends ConDecDashboardItem {

	@Override
	public Map<String, Object> getMetrics() {
		Map<String, Object> metrics = new LinkedHashMap<>();

		List<Project> projects = DecisionKnowledgeProject.getProjectsWithConDecActivatedAndAccessableForUser(user);
		metrics.put("projects", projects);

		List<Project> accessableProjectsWithGitRepo = new ArrayList<>();
		for (Project project : projects) {
			String projectKey = project.getKey();
			if (ConfigPersistenceManager.isKnowledgeExtractedFromGit(projectKey)) {
				accessableProjectsWithGitRepo.add(project);
			}
		}

		metrics.put("projectsWithGit", accessableProjectsWithGitRepo);

		return metrics;
	}

}
