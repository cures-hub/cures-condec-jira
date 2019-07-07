package de.uhd.ifi.se.decision.management.jira.filtering;

import java.util.List;

import com.atlassian.jira.issue.Issue;
import com.atlassian.query.Query;

/**
 * Interface to handle queries in JIRA, either written in JIRA Query Language
 * (JQL) or as a preset filter (can also be set by the user).
 */
public interface JiraQueryHandler {

	/**
	 * Returns all JIRA issues that match the query.
	 * 
	 * @return list of JIRA issues that match the query.
	 */
	List<Issue> getJiraIssuesFromQuery();

	/**
	 * Returns the names of the JIRA issue types explicitly mentioned in the query,
	 * e.g. "issuetype in (Decision, Issue)". Returns an empty list for the query
	 * "type != null".
	 * 
	 * @return names of the JIRA issue types explicitly mentioned in the query.
	 */
	List<String> getNamesOfJiraIssueTypesInQuery();

	/**
	 * Returns the earliest creation date (start date) that JIRA issues are included
	 * as a long value or -1 if empty.
	 * 
	 * @return creation start date as a long value or -1 if empty.
	 */
	long getCreatedEarliest();

	/**
	 * Returns the latest creation date (end date) that JIRA issues are included as
	 * a long value or -1 if empty.
	 * 
	 * @return creation end date as a long value or -1 if empty.
	 */
	long getCreatedLatest();

	/**
	 * Returns the query as a String. If a filter was inserted in the constructor,
	 * the respective JQL query is returned.
	 * 
	 * @return query as a String.
	 */
	String getQuery();

	/**
	 * Returns the query object.
	 * 
	 * @return query object.
	 */
	Query getQueryObject();

	/**
	 * Returns the query type. This can either be a query in JIRA Query Language
	 * (JQL), a preset filter (can also be set by the user), or could be of any
	 * other format.
	 * 
	 * @see JiraQueryType
	 * @return query type, either JQL, FILTER, or OTHER if unknown.
	 */
	JiraQueryType getQueryType();
}