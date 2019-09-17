package de.uhd.ifi.se.decision.management.jira.extraction;

import org.eclipse.jgit.revwalk.RevCommit;

import com.atlassian.jira.issue.Issue;

import de.uhd.ifi.se.decision.management.jira.model.git.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.model.git.Diff;

/**
 * Interface to create a summary of code changes linked to Jira issues (e.g. to work items).
 */
public interface CodeSummarizer {

	/**
	 * Creates a summary of code changes for all commits associated to a JIRA issue.
	 * 
	 * @param jiraIssue
	 *            JIRA issue. Its key is searched for in commit messages.
	 * @param probabilityOfCorrectness
	 *            probabilityOfCorrectness. Integer value for filter over
	 *            correctness
	 * @return summary as a String.
	 */
	String createSummary(Issue jiraIssue, int probabilityOfCorrectness);

	/**
	 * Creates a summary of code changes for a diff.
	 * 
	 * @param diff
	 *            object of {@link Diff} class containing {@link ChangedFile}s.
	 * @return summary as a String.
	 */
	String createSummary(Diff diff);

	/**
	 * Creates a summary of code changes for a commit.
	 * 
	 * @param commit
	 *            commit with code changes as a RevCommit object.
	 * @return summary as a String.
	 */
	String createSummary(RevCommit commit);
}