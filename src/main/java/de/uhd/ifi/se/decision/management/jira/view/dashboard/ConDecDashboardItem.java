package de.uhd.ifi.se.decision.management.jira.view.dashboard;

import java.util.HashMap;
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
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;

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
		String linkDistance = request.getParameter("linkDistance");
		newContext.put("linkDistance", linkDistance);
		if (linkDistance != null) {
			filterSettings.setLinkDistance(Integer.parseInt(linkDistance));
		}

		newContext.putAll(getAdditionalParameters());
		newContext.putAll(getMetrics());

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
		return new HashMap<>();
	}

	protected Map<String, Object> getMetrics() {
		return new HashMap<>();
	}
}
