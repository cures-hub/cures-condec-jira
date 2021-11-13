package de.uhd.ifi.se.decision.management.jira.git.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Models a list of {@link ChangedFile}s. The scope for the diff might be a
 * single git commit, a whole feature branch (with many commits), or all commits
 * belonging to a Jira issue.
 */
public class Diff {

	private List<ChangedFile> changedFiles;

	public Diff() {
		changedFiles = new ArrayList<ChangedFile>();
	}

	public Diff(ChangedFile codeFile) {
		this();
		addChangedFile(codeFile);
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

	public void add(Diff diff) {
		for (ChangedFile changedFile : diff.getChangedFiles()) {
			addChangedFile(changedFile);
		}
	}

	/**
	 * @return decision knowledge elements documented of the {@link ChangedFile}s
	 *         that are part of this diff.
	 * @see ChangedFile#getRationaleElementsFromCodeComments()
	 */
	public List<DecisionKnowledgeElementInCodeComment> getRationaleElementsFromCodeComments() {
		List<DecisionKnowledgeElementInCodeComment> elementsFromCode = new ArrayList<>();
		for (ChangedFile codeFile : getChangedFiles()) {
			elementsFromCode.addAll(codeFile.getRationaleElementsFromCodeComments());
		}
		return elementsFromCode;
	}
}
