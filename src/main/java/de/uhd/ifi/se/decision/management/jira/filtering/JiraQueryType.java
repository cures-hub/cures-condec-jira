package de.uhd.ifi.se.decision.management.jira.filtering;

/**
 * Type of search term in JIRA.
 */
public enum JiraQueryType {
	JQL, FILTER, OTHER;

	public static JiraQueryType getJiraQueryType(String searchTerm) {
		JiraQueryType jiraQueryType = OTHER;
		if (searchTerm.indexOf("filter") == 1) {
			jiraQueryType = FILTER;
		}
		if (searchTerm.indexOf("jql") == 1) {
			jiraQueryType = JQL;
		}
		return jiraQueryType;
	}
}
