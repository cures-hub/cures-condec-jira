package de.uhd.ifi.se.decision.management.jira.view.dashboard;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.ContextProvider;
import com.google.common.collect.Maps;

import de.uhd.ifi.se.decision.management.jira.config.JiraSchemeManager;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.metric.BranchMetricCalculator;
import de.uhd.ifi.se.decision.management.jira.metric.GeneralMetricCalculator;
import de.uhd.ifi.se.decision.management.jira.metric.RationaleCompletenessCalculator;
import de.uhd.ifi.se.decision.management.jira.metric.RationaleCoverageCalculator;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;

/**
 * Abstract super class for all dashboard items that present metrics calculated
 * on the {@link KnowledgeGraph} data structure. To create a dashboard item, one
 * needs to register the dashboard item in the atlassian-plugin.xml and
 * reference this class.
 * 
 * @see GeneralMetricCalculator
 * @see RationaleCompletenessCalculator
 * @see RationaleCoverageCalculator
 * @see BranchMetricCalculator
 * 
 * @issue How are metrics passed from backend to the frontend?
 * @alternative We used to pass the metrics via context parameters of the Java
 *              servlet.
 * @con The dashboard page needs to be reloaded to update the metrics (e.g.
 *      after filtering).
 * @decision We pass the metrics via the DashboardRest API!
 * @pro Faster updating of metrics. The dashboard page does not need to be
 *      reloaded.
 */
public class ConDecDashboardItem implements ContextProvider {
	protected ApplicationUser user;
	protected FilterSettings filterSettings;
	protected IssueType jiraIssueType;

	protected static final Logger LOGGER = LoggerFactory.getLogger(ConDecDashboardItem.class);

	@Override
	public void init(Map<String, String> params) throws PluginParseException {
		/**
		 * No special behaviour is foreseen for now.
		 */
	}

	@Override
	public Map<String, Object> getContextMap(Map<String, Object> context) {
		Map<String, Object> newContext = Maps.newHashMap(context);

		user = getApplicationUser();

		HttpServletRequest request = getHttpRequest();
		String projectKey = "";
		String issueTypeId = "0";
		if (request != null) {
			projectKey = request.getParameter("project");
			issueTypeId = request.getParameter("issueType");
		}
		newContext.put("projectKey", projectKey);
		jiraIssueType = JiraSchemeManager.getJiraIssueType(issueTypeId);
		newContext.put("issueType", jiraIssueType);
		newContext.put("jiraIssueTypes", new JiraSchemeManager(projectKey).getJiraIssueTypes());
		newContext.put("jiraBaseUrl", ComponentAccessor.getApplicationProperties().getString(APKeys.JIRA_BASEURL));

		if (projectKey == null || projectKey.isBlank()) {
			List<Project> projects = DecisionKnowledgeProject.getProjectsWithConDecActivatedAndAccessableForUser(user);
			newContext.put("projects", projects);
			return newContext;
		}

		filterSettings = new FilterSettings(projectKey, "");

		newContext.putAll(getAdditionalParameters());

		LOGGER.info(filterSettings.toString());
		return newContext;
	}

	public static HttpServletRequest getHttpRequest() {
		return com.atlassian.jira.web.ExecutingHttpRequest.get();
	}

	public static ApplicationUser getApplicationUser() {
		if (ComponentAccessor.getJiraAuthenticationContext() != null) {
			return ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
		}
		return null;
	}

	protected Map<String, Object> getAdditionalParameters() {
		Map<String, Object> additionalParameters = new LinkedHashMap<>();

		List<Project> projects = DecisionKnowledgeProject.getProjectsWithConDecActivatedAndAccessableForUser(user);
		additionalParameters.put("projects", projects);

		List<Project> accessableProjectsWithGitRepo = new ArrayList<>();
		for (Project project : projects) {
			String projectKey = project.getKey();
			if (ConfigPersistenceManager.getGitConfiguration(projectKey).isActivated()) {
				accessableProjectsWithGitRepo.add(project);
			}
		}

		additionalParameters.put("projectsWithGit", accessableProjectsWithGitRepo);

		return additionalParameters;
	}
}
