package de.uhd.ifi.se.decision.management.jira.filtering;

/**
 * Type of preset filters in JIRA.
 */
public enum JiraFilter {

	MYOPENISSUES("assignee = currentUser() AND resolution = Unresolved"), // My open issues
	REPORTEDBYME("reporter = currentUser()"), // Reported by me
	RECENTLYVIEWFINALQUERYED("issuekey IN issueHistory()"), // Viewed recently
	ALLISSUES("type != null"), // All issues (default)
	ALLOPENISSUES("resolution = Unresolved"), // Open issues
	ADDEDRECENTLY("created >= -1w"), // Created recently
	UPDATEDRECENTLY("updated >= -1w"), // Updated recently;
	RESOLVEDRECENTLY("resolutiondate >= -1w"), // Resolved recently
	DONEISSUES("statusCategory = Done"); // Done issues

	private String jqlString;

	private JiraFilter(String jqlString) {
		this.jqlString = jqlString;
	}

	/**
	 * Returns the JIRA query as a String.
	 *
	 * @return JIRA query as a String.
	 */
	public String getJqlString() {
		return this.jqlString;
	}

	public static String getQueryFromPresetFilter(String filterQuery) {
		JiraFilter jiraFilter = JiraFilter.valueOf(filterQuery);
		if (jiraFilter == null) {
			jiraFilter = ALLISSUES;
		}
		return jiraFilter.getJqlString();
	}
}
