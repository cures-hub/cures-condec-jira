package de.uhd.ifi.se.decision.management.jira.extraction;

import java.util.Map;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.revwalk.RevCommit;

public interface CodeSummarizer {

	/**
	 * Creates a summary of code changes for all commits associated to a JIRA issue.
	 * 
	 * @param jiraIssueKey
	 *            JIRA issue key that is searched for in commit messages.
	 * @return summary as a String.
	 */
	String createSummary(String jiraIssueKey, int probability);

	/**
	 * Creates a summary of code changes for a diff.
	 * 
	 * @param diff
	 *            map of diff entries and the respective edit lists.
	 * @return summary as a String.
	 */
	String createSummary(Map<DiffEntry, EditList> diff);

	/**
	 * Creates a summary of code changes for a commit.
	 * 
	 * @param commit
	 *            commit with code changes as a RevCommit object.
	 * @return summary as a String.
	 */
	String createSummary(RevCommit commit);
}