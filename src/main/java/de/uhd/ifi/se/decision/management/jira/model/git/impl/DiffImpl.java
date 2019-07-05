package de.uhd.ifi.se.decision.management.jira.model.git.impl;

import java.util.ArrayList;
import java.util.List;

import de.uhd.ifi.se.decision.management.jira.model.git.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.model.git.Diff;

/**
 * Class for a list of changed files. The scope for the diff might be a single
 * git commit, a whole feature branch (with many commits), or all commits
 * belonging to a JIRA issue.
 */
public class DiffImpl implements Diff {

	private List<ChangedFile> changedFiles;

	public DiffImpl() {
		this.changedFiles = new ArrayList<ChangedFile>();
	}

	@Override
	public List<ChangedFile> getChangedFiles() {
		return changedFiles;
	}

	@Override
	public void addChangedFile(ChangedFile changedFile) {
		changedFiles.add(changedFile);
	}
}
