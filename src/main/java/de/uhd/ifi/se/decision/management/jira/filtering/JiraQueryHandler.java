package de.uhd.ifi.se.decision.management.jira.filtering;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.bc.issue.search.SearchService.ParseResult;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.query.Query;

/**
 * Handles queries in Jira, either written in Jira Query Language (JQL) or as a
 * preset filter (can also be set by the user). Helps to parse a query into a
 * {@link FilterSettings} object.
 */
public class JiraQueryHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(JiraQueryHandler.class);

	private SearchService searchService;
	private ApplicationUser user;
	private String query;
	private String projectKey;
	private JiraQueryType queryType;

	public JiraQueryHandler(ApplicationUser user, String projectKey, String query) {
		this.searchService = ComponentAccessor.getComponentOfType(SearchService.class);
		this.user = user;
		this.projectKey = projectKey;
		this.queryType = JiraQueryType.getJiraQueryType(query);
		this.query = getFinalQuery(query, queryType);
	}

	private String getFinalQuery(String query, JiraQueryType queryType) {
		String finalQuery = getRawQuery(query);
		switch (queryType) {
		case FILTER:
			finalQuery = finalQuery.substring(8);
			finalQuery = "?jql=" + JiraFilter.getQueryForFilter(finalQuery);
			break;
		case JQL:
			finalQuery = cleanDirtyJqlString(finalQuery);
			break;
		default:
			finalQuery = "?jql=resolution = Unresolved";
			break;
		}
		finalQuery = appendProjectKey(finalQuery);
		return finalQuery;
	}

	private String appendProjectKey(String query) {
		if (query.contains("project") || projectKey == null || projectKey.isBlank()) {
			return query;
		}
		return query + " AND project = " + projectKey;
	}

	/**
	 * The searchTerm might start with "abc§" but should start with "?". This
	 * method replaces "abc§" with "?".
	 */
	private String getRawQuery(String searchString) {
		if (searchString == null || searchString.isEmpty()) {
			return "?jql = resolution = Unresolved";
		}
		String croppedQuery = searchString;
		String[] split = searchString.split("§");
		if (split.length > 1) {
			croppedQuery = "?" + split[1];
		}
		return croppedQuery;
	}

	private String cleanDirtyJqlString(String jql) {
		return jql.replaceAll("%20", " ").replaceAll("%3D", "=").replaceAll("%2C", ",");
	}

	/**
	 * @return names of the Jira issue types explicitly mentioned in the query. e.g.
	 *         "issuetype in (Decision, Issue)". Returns an empty list for the query
	 *         "type != null".
	 */
	public Set<String> getNamesOfJiraIssueTypesInQuery() {
		return getNamesOfJiraIssueTypesInQuery(query);
	}

	/**
	 * @return names of the Jira issue types explicitly mentioned in the query. e.g.
	 *         "issuetype in (Decision, Issue)". Returns an empty list for the query
	 *         "type != null".
	 */
	public static Set<String> getNamesOfJiraIssueTypesInQuery(String query) {
		if (!query.contains("type")) {
			return new HashSet<String>();
		}
		Set<String> types = new LinkedHashSet<String>();
		String queryPartContainingTypes = query.split("type")[1];
		queryPartContainingTypes = queryPartContainingTypes.split("AND")[0];

		// issuetype = Decision
		if (queryPartContainingTypes.contains("=")) {
			String split[] = queryPartContainingTypes.split("=");
			types.add(split[1].trim().replaceAll("\"", ""));
			return types;
		}

		// issuetype in (Decision, Issue, ...)
		String issueTypesSeparated = queryPartContainingTypes.split("AND")[0];
		String issueTypesCleared = issueTypesSeparated.replaceAll("[()]", "").replaceAll("\"", "").replaceAll("in", "");
		String[] split = issueTypesCleared.split(",");
		for (String issueType : split) {
			issueType = issueType.replaceAll("[()]", "");
			types.add(issueType.trim());
		}
		return types;
	}

	/**
	 * @return earliest creation date (start date) that Jira issues are included in
	 *         the filter result as a long value or -1 if empty.
	 */
	public long getCreatedEarliest() {
		if (!query.contains("created") || !query.contains(" >= ")) {
			return -1;
		}
		String timeSubstringOfQuery = getQuerySubstringWithTimeInformation().split(">=")[1].trim();
		long startTime = 0;
		if (timeSubstringOfQuery.matches("(\\d\\d\\d\\d-\\d\\d-\\d\\d)(.)*")) {
			// created >= 1970-01-01
			startTime = getDateFromYearMonthDateFormat(timeSubstringOfQuery);
		} else if (timeSubstringOfQuery.matches("(-\\d+(.)*)")) {
			// created >= -1w
			long currentDate = new Date().getTime();
			startTime = getTimeFromNumberAndFactorLetter(currentDate, timeSubstringOfQuery);
		}
		return startTime;
	}

	private String getQuerySubstringWithTimeInformation() {
		return query.substring(query.indexOf("created"), query.length());
	}

	/**
	 * @return latest creation date (end date) that Jira issues are included in the
	 *         filter result as a long value or -1 if empty.
	 */
	public long getCreatedLatest() {
		if (!query.contains("created") || !query.contains(" <= ")) {
			return -1;
		}
		String timeSubstringOfQuery = getQuerySubstringWithTimeInformation().split("<=")[1].trim();
		long endTime = 0;
		if (timeSubstringOfQuery.matches("(\\d\\d\\d\\d-\\d\\d-\\d\\d)(.)*")) {
			// created <= 1970-01-01
			endTime = getDateFromYearMonthDateFormat(timeSubstringOfQuery);
		} else if (timeSubstringOfQuery.matches("-\\d+(.)*")) {
			// created <= -1w
			long currentDate = new Date().getTime();
			endTime = getTimeFromNumberAndFactorLetter(currentDate, timeSubstringOfQuery);
		}
		return endTime;
	}

	private long getDateFromYearMonthDateFormat(String dateAsString) {
		long dateAsLong = -1;
		try {
			DateFormat simple = new SimpleDateFormat("yyyy-MM-dd");
			Date date = simple.parse(dateAsString);
			dateAsLong = date.getTime();
		} catch (ParseException e) {
			LOGGER.error(e.getMessage());
		}
		return dateAsLong;
	}

	private long getTimeFromNumberAndFactorLetter(long currentDate, String dateAsNumberAndLetter) {
		long factor;
		String clearedTime = dateAsNumberAndLetter.replaceAll("\\s(.)+", "");
		char factorLetter = clearedTime.charAt(clearedTime.length() - 1);
		factor = getTimeFactor(factorLetter);
		long queryTime = currentDate + 1;
		String queryTimeString = dateAsNumberAndLetter.replaceAll("\\D+", "");
		try {
			queryTime = Long.parseLong(queryTimeString);
		} catch (NumberFormatException e) {
			LOGGER.error("No valid time was given in the Jira query.");
		}
		long result = currentDate - (queryTime * factor);
		return result;
	}

	private long getTimeFactor(char factorAsALetter) {
		switch (factorAsALetter) {
		case 'm':
			return 60000;
		case 'h':
			return 3600000;
		case 'd':
			return 86400000;
		case 'w':
			return 604800000;
		default:
			return 1;
		}
	}

	/**
	 * @return query as a String. If a filter was inserted in the constructor, the
	 *         respective JQL query is returned.
	 */
	public String getQuery() {
		return query;
	}

	/**
	 * @return query object.
	 */
	public Query getQueryObject() {
		return getParseResult().getQuery();
	}

	/**
	 * @return {@link JiraQueryType}, either JQL, FILTER, or OTHER if unknown. This
	 *         can either be a query in Jira Query Language (JQL), a preset filter
	 *         (can also be set by the user), or could be of any other format.
	 */
	public JiraQueryType getQueryType() {
		return queryType;
	}

	/**
	 * @return list of Jira issues that match the query.
	 */
	public List<Issue> getJiraIssuesFromQuery() {
		ParseResult parseResult = getParseResult();
		if (!parseResult.isValid()) {
			LOGGER.error("Getting Jira issues from JQL query failed. " + parseResult.getErrors().toString());
			return new ArrayList<Issue>();
		}

		List<Issue> jiraIssues = new ArrayList<Issue>();
		try {
			SearchResults<Issue> results = searchService.search(user, parseResult.getQuery(),
					PagerFilter.getUnlimitedFilter());
			jiraIssues = results.getResults();
		} catch (SearchException e) {
			LOGGER.error("Getting Jira issues from JQL query failed. Message: " + e.getMessage());
		}
		return jiraIssues;
	}

	private ParseResult getParseResult() {
		return searchService.parseQuery(this.user, query.substring(5));
	}
}
