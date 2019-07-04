package de.uhd.ifi.se.decision.management.jira.model.git;

import java.util.List;

/**
 * Interface for a list of changed files. The scope for the diff might be a
 * single git commit, a whole feature branch (with many commits), or all commits
 * belonging to a JIRA issue.
 */
public interface Diff {

	/**
	 * Returns the files changed in the diff as a list of {@link ChangedFile}
	 * objects.
	 * 
	 * @return list of {@link ChangedFile} objects.
	 */
	List<ChangedFile> getChangedFiles();

	/**
	 * Adds a new {@link ChangedFile} to the diff.
	 * 
	 * @param changedFile
	 *            object of {@link ChangedFile} class.
	 */
	void addChangedFile(ChangedFile changedFile);
}
