package de.uhd.ifi.se.decision.management.jira.view.dashboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.ContextProvider;
import com.google.common.collect.Maps;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;

public class FeatureBranchQualityDashboardItem implements ContextProvider {

	@Override
	public void init(Map<String, String> params) throws PluginParseException {
		/**
		 * No special behaviour is foreseen for now.
		 */
	}

	@Override
	public Map<String, Object> getContextMap(Map<String, Object> context) {
		Map<String, Object> newContext = Maps.newHashMap(context);

		ApplicationUser user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
		List<Project> accessableProjects = DecisionKnowledgeProject
				.getProjectsWithConDecActivatedAndAccessableForUser(user);
		newContext.put("projects", accessableProjects);

		List<Project> accessableProjectsWithGitRepo = new ArrayList<>();
		for (Project project : accessableProjects) {
			String projectKey = project.getKey();
			if (ConfigPersistenceManager.isKnowledgeExtractedFromGit(projectKey)) {
				accessableProjectsWithGitRepo.add(project);
			}
		}
		newContext.put("projectsWithGit", accessableProjectsWithGitRepo);

		return newContext;
	}
}
