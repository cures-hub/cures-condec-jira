package de.uhd.ifi.se.decision.management.jira.filtering;

/**
 * Type of search string in JIRA. A search string can either contain a query in
 * JIRA Query Language (JQL), a preset filter (can also be set by the user), or
 * can be of any other format.
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
