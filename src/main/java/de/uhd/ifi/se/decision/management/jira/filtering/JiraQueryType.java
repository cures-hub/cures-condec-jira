package de.uhd.ifi.se.decision.management.jira.filtering;

/**
 * Type of search string in JIRA. A search string can either contain a query in
 * JIRA Query Language (JQL), a preset filter (can also be set by the user), or
 * could be of any other format.
 */
public enum JiraQueryType {
	JQL, // matches("\\?jql=(.)+")
	FILTER, // matches("\\?filter=(.)+")
	OTHER;

	public static JiraQueryType getJiraQueryType(String searchTerm) {
		if (searchTerm == null) {
			return OTHER;
		}

		JiraQueryType jiraQueryType = OTHER;
		if (searchTerm.contains("jql")) {
			jiraQueryType = JQL;
		}
		if (searchTerm.contains("filter")) {
			jiraQueryType = FILTER;
		}
		return jiraQueryType;
	}
}
