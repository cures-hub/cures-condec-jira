package de.uhd.ifi.se.decision.management.jira.extraction;

import java.util.Map;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.EditList;

public interface CodeSummarizer {

	/**
	 * Creates a summary of code changes for a JIRA issue.
	 * 
	 * @param jiraIssueKey
	 *            JIRA issue key that is searched for in commit messages.
	 * @return summary as a String.
	 */
	String createSummary(String jiraIssueKey);

	/**
	 * Creates a summary of code changes for a diff.
	 * 
	 * @param diff
	 *            map of diff entries and the respective edit lists.
	 * @return summary as a String.
	 */
	String createSummary(Map<DiffEntry, EditList> diff);
}