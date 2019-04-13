package de.uhd.ifi.se.decision.management.jira.persistence;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.search.SearchResults;

/**
 * Helper class to support both JIRA versions 7 and 8.
 */
public class JiraSearchServiceHelper {

	@SuppressWarnings("unchecked")
	public static List<Issue> getJiraIssues(SearchResults searchResults) {
		List<Issue> jiraIssues = new ArrayList<Issue>();
		// THIS IS THE OLD CODE - only running against jira 7.x
		// jiraIssues.addAll(searchResult.getIssues());

		Method newGetMethod = null;
		try {
			newGetMethod = SearchResults.class.getMethod("getIssues");
		} catch (NoSuchMethodException e) {
			try {
				newGetMethod = SearchResults.class.getMethod("getResults");
			} catch (NoSuchMethodError | NoSuchMethodException | SecurityException e2) {
			}
		}

		if (newGetMethod != null) {
			try {
				jiraIssues.addAll((List<Issue>) newGetMethod.invoke(searchResults));
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		} else {
			throw new RuntimeException("ICT: SearchResults Service from JIRA NOT AVAILABLE (getIssue / getResults)");
		}

		return jiraIssues;
	}
}
