package de.uhd.ifi.se.decision.management.jira.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.ofbiz.core.entity.GenericValue;

import com.atlassian.configurable.ValuesGenerator;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.fields.config.manager.IssueTypeSchemeManager;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.project.Project;

/**
 * Provides a list of Jira issue types for the decision knowledge report. The
 * user needs to select one from this list.
 */
public class JiraIssueTypeGenerator implements ValuesGenerator<String> {

	@Override
	@SuppressWarnings("rawtypes")
	// @issue: How can we get the project id for the selected project? Is the
	// projectId part of params?
	// @decision: Use (GenericValue) params.get("project");
	public Map<String, String> getValues(Map params) {
		if (params == null || params.size() == 0) {
			return new HashMap<String, String>();
		}
		GenericValue valueProject = (GenericValue) params.get("project");
		if (valueProject == null) {
			return new HashMap<String, String>();
		}

		long projectId = (long) valueProject.get("id");

		Collection<IssueType> jiraIssueTypesList = getJiraIssueTypes(projectId);
		Map<String, String> jiraIssueTypes = new HashMap<String, String>();

		for (IssueType type : jiraIssueTypesList) {
			jiraIssueTypes.put(type.getId(), type.getName());
		}

		return jiraIssueTypes;
	}

	public static Collection<IssueType> getJiraIssueTypes(long projectId) {
		if (projectId <= 0) {
			return new ArrayList<IssueType>();
		}
		IssueTypeSchemeManager issueTypeSchemeManager = ComponentAccessor.getIssueTypeSchemeManager();
		Project project = ComponentAccessor.getProjectManager().getProjectObj(projectId);
		return issueTypeSchemeManager.getIssueTypesForProject(project);
	}

	public static Collection<IssueType> getJiraIssueTypes(String projectKey) {
		if (projectKey == null) {
			return new ArrayList<IssueType>();
		}
		IssueTypeSchemeManager issueTypeSchemeManager = ComponentAccessor.getIssueTypeSchemeManager();
		Project project = ComponentAccessor.getProjectManager().getProjectObjByKey(projectKey);
		return issueTypeSchemeManager.getIssueTypesForProject(project);
	}

	public static String getJiraIssueTypeName(String typeId) {
		if (typeId == null || typeId.equals("")) {
			return "";
		}
		IssueType issueType = ComponentAccessor.getConstantsManager().getIssueType(typeId);
		if (issueType == null) {
			return "";
		}
		return issueType.getName();
	}
}
