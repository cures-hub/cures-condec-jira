package de.uhd.ifi.se.decision.management.jira.view;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
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
	public void init(final Map<String, String> params) throws PluginParseException {
		/**
		 * No special behaviour is foreseen for now.
		 */
	}

	@Override
	public Map<String, Object> getContextMap(final Map<String, Object> context) {
		final Map<String, Object> newContext = Maps.newHashMap(context);

		Map<String, Object> projectContext = attachProjectsMaps();
		newContext.putAll(projectContext);

		SecureRandom random = new SecureRandom();
		String uid = String.valueOf(random.nextInt(10000));
		String selectId = "condec-dashboard-item-project-selection" + uid;
		newContext.put("selectID", selectId);
		newContext.put("dashboardUID", uid);

		return newContext;
	}

	private Map<String, Object> attachProjectsMaps() {
		Map<String, Object> newContext = new HashMap<>();
		ApplicationUser user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
		List<Project> accessableProjects = DecisionKnowledgeProject
				.getProjectsWithConDecActivatedAndAccessableForUser(user);
		List<Project> accessableProjectsWithGitRepo = new ArrayList<>();
		for (Project project : accessableProjects) {
			String projectKey = project.getKey();
			if (ConfigPersistenceManager.isKnowledgeExtractedFromGit(projectKey)) {
				accessableProjectsWithGitRepo.add(project);
			}
		}
		newContext.put("projects", accessableProjects);
		newContext.put("projectsWithGit", accessableProjectsWithGitRepo);

		return newContext;
	}
}
