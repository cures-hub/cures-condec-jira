package de.uhd.ifi.se.decision.management.jira.filtering;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.search.SearchRequest;
import com.atlassian.jira.issue.search.SearchRequestManager;

/**
 * Type of preset filters in JIRA.
 */
public enum JiraFilter {

	MYOPENISSUES(-1, "assignee = currentUser() AND resolution = Unresolved"), // My open issues
	REPORTEDBYME(-2, "reporter = currentUser()"), // Reported by me
	RECENTLYVIEWFINALQUERYED(-3, "issuekey IN issueHistory()"), // Viewed recently
	ALLISSUES(-4, "type != null"), // All issues (default)
	ALLOPENISSUES(-5, "resolution = Unresolved"), // Open issues
	ADDEDRECENTLY(-6, "created >= -1w"), // Created recently
	UPDATEDRECENTLY(-7, "updated >= -1w"), // Updated recently;
	RESOLVEDRECENTLY(-8, "resolutiondate >= -1w"), // Resolved recently
	DONEISSUES(-9, "statusCategory = Done"); // Done issues
	
	private static final Logger LOGGER = LoggerFactory.getLogger(JiraFilter.class);

	private long id;
	private String jqlString;

	private JiraFilter(long id, String jqlString) {
		this.id = id;
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

	/**
	 * Returns the number of the JIRA filter.
	 *
	 * @return number of the JIRA filter.
	 */
	public long getId() {
		return this.id;
	}

	public static JiraFilter valueOf(long id) {
		for (JiraFilter jiraFilter : values()) {
			if (jiraFilter.id == id) {
				return jiraFilter;
			}
		}
		return ALLISSUES;
	}

	public static String getQueryFromFilterName(String filterName) {
		JiraFilter jiraFilter = ALLISSUES;
		try {
			jiraFilter = JiraFilter.valueOf(filterName.toUpperCase(Locale.ENGLISH));
		} catch (IllegalArgumentException e) {
			LOGGER.error("The filter " + filterName + " could not be converted into a query String.");
		}
		return jiraFilter.getJqlString();
	}

	public static String getQueryFromFilterId(long filterId, String projectKey) {
		if (!isPresetJiraFilter(filterId)) {
			return getQueryForCustomFilter(filterId);
		}
		JiraFilter jiraFilter = valueOf(filterId);
		String query = jiraFilter.getJqlString();
		query = "project = " + projectKey + " AND " + query;
		return query;
	}

	private static boolean isPresetJiraFilter(long filterId) {
		return filterId <= 0;
	}

	private static String getQueryForCustomFilter(long filterId) {
		SearchRequestManager searchRequestManager = ComponentAccessor.getComponentOfType(SearchRequestManager.class);
		SearchRequest filter = searchRequestManager.getSharedEntity(filterId);
		return filter.getQuery().getQueryString();
	}
}
