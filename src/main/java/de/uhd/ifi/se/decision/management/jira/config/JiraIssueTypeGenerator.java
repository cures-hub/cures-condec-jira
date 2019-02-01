package de.uhd.ifi.se.decision.management.jira.config;

import java.util.HashMap;
import java.util.Map;

import com.atlassian.jira.portal.SortingValuesGenerator;

public class JiraIssueTypeGenerator extends SortingValuesGenerator {

	@Override
	@SuppressWarnings("rawtypes")
	public Map<String, String> getValues(Map params) {
		Map<String, String> jiraIssueTypes = new HashMap<String, String>();
		jiraIssueTypes.put("WI", "Work Item");
		jiraIssueTypes.put("B", "Bug");
		jiraIssueTypes.put("T", "Task");
		return jiraIssueTypes;
	}
}
