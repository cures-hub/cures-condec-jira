package de.uhd.ifi.se.decision.management.jira.extraction.impl;

import java.util.ArrayList;
import java.util.List;

import de.uhd.ifi.se.decision.management.jira.extraction.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.extraction.Diff;

public class DiffImpl implements Diff {
	private List<ChangedFile> changedFiles;

	public DiffImpl() {
		this.changedFiles = new ArrayList<ChangedFile>();
	}

	public List<ChangedFile> getChangedFiles() {
		return changedFiles;
	}

	public void addChangedFile(ChangedFile changedFile) {
		changedFiles.add(changedFile);
	}
}
