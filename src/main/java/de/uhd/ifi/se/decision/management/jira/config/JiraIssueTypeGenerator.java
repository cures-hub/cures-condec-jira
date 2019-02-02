package de.uhd.ifi.se.decision.management.jira.config;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.IssueTypeManager;
import com.atlassian.jira.issue.fields.config.manager.IssueTypeSchemeManager;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.portal.SortingValuesGenerator;
import com.atlassian.jira.project.Project;

/**
 * Provides the JIRA issue types for the decision knowledge report.
 */
public class JiraIssueTypeGenerator extends SortingValuesGenerator {

	@Override
	@SuppressWarnings("rawtypes")
	// @issue: How can we get the project id for the selected project? Is the projectId part of params?
	public Map<String, String> getValues(Map params) {
		Map<String, String> jiraIssueTypes = new HashMap<String, String>();

		IssueTypeManager issueTypeManager = ComponentAccessor.getComponent(IssueTypeManager.class);
		Collection<IssueType> types = issueTypeManager.getIssueTypes();

		for (IssueType type : types) {
			jiraIssueTypes.put(type.getId(), type.getName());
		}

		return jiraIssueTypes;
	}

	public static Collection<IssueType> getJiraIssueTypes(long projectId) {
		IssueTypeSchemeManager issueTypeSchemeManager = ComponentAccessor.getIssueTypeSchemeManager();
		Project project = ComponentAccessor.getProjectManager().getProjectObj(projectId);
		return issueTypeSchemeManager.getIssueTypesForProject(project);
	}

	public static String getJiraIssueTypeName(String typeId) {
		IssueType issueType = ComponentAccessor.getConstantsManager().getIssueType(typeId);
		return issueType.getName();
	}
}
