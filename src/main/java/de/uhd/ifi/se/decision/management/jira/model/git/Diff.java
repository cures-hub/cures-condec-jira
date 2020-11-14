package de.uhd.ifi.se.decision.management.jira.model.git;

import java.util.ArrayList;
import java.util.List;

/**
 * Models a list of changed files. The scope for the diff might be a single git
 * commit, a whole feature branch (with many commits), or all commits belonging
 * to a Jira issue.
 */
public class Diff {

	private List<ChangedFile> changedFiles;

	public Diff() {
		changedFiles = new ArrayList<ChangedFile>();
	}

	/**
	 * @return files changed in the diff as a list of {@link ChangedFile} objects.
	 */
	public List<ChangedFile> getChangedFiles() {
		return changedFiles;
	}

	/**
	 * Adds a new {@link ChangedFile} to the diff.
	 * 
	 * @param changedFile
	 *            object of {@link ChangedFile} class.
	 */
	public void addChangedFile(ChangedFile changedFile) {
		changedFiles.add(changedFile);
	}
}
