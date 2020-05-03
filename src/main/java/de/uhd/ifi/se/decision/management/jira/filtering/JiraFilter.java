package de.uhd.ifi.se.decision.management.jira.filtering;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.search.SearchRequest;
import com.atlassian.jira.issue.search.SearchRequestManager;

/**
 * Type of preset filters in Jira. This enum also contains static methods to
 * handle custom filters created by a user.
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
	private String jql;

	private JiraFilter(long id, String jql) {
		this.id = id;
		this.jql = jql;
	}

	/**
	 * @return Jira query as a String.
	 */
	public String getJqlString() {
		return this.jql;
	}

	/**
	 * @return number of the Jira filter.
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

	public static String getQueryForFilterName(String filterName) {
		JiraFilter jiraFilter = ALLISSUES;
		try {
			jiraFilter = JiraFilter.valueOf(filterName.toUpperCase(Locale.ENGLISH));
		} catch (IllegalArgumentException e) {
			LOGGER.error("The filter " + filterName + " could not be converted into a query String.");
		}
		return jiraFilter.getJqlString();
	}

	public static String getQueryForFilterId(long filterId) {
		if (!isPresetJiraFilter(filterId)) {
			return getQueryForCustomFilter(filterId);
		}
		JiraFilter jiraFilter = valueOf(filterId);
		return jiraFilter.getJqlString();
	}

	private static boolean isPresetJiraFilter(long filterId) {
		return filterId <= 0;
	}

	/**
	 * @param filterId
	 *            id of a custom filter created by a user. This id needs to be >0.
	 * @return Jira query as a String.
	 */
	private static String getQueryForCustomFilter(long filterId) {
		SearchRequestManager searchRequestManager = ComponentAccessor.getComponentOfType(SearchRequestManager.class);
		SearchRequest filter = searchRequestManager.getSharedEntity(filterId);
		return filter.getQuery().getQueryString();
	}

	public static String getQueryForFilter(String searchTerm) {
		long filterId = 0;
		boolean filterIsNumberCoded = false;
		try {
			filterId = Long.parseLong(searchTerm, 10);
			filterIsNumberCoded = true;
		} catch (NumberFormatException e) {
			LOGGER.error("Produce results from query failed. Message: " + e.getMessage());
		}
		if (filterIsNumberCoded) {
			return JiraFilter.getQueryForFilterId(filterId);
		}
		return JiraFilter.getQueryForFilterName(searchTerm);
	}

	public static boolean containsJiraFilter(String searchTerm) {
		return searchTerm.indexOf("filter") == 1;
	}
}
