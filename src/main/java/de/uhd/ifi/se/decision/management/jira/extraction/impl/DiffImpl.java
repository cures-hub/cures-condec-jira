package de.uhd.ifi.se.decision.management.jira.extraction.impl;

import java.util.ArrayList;
import java.util.List;

import de.uhd.ifi.se.decision.management.jira.extraction.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.extraction.Diff;

public class DiffImpl implements Diff {
	private ArrayList<ChangedFile> changedFiles;

	public DiffImpl() {
		this.changedFiles = new ArrayList<ChangedFile>();
	}

	public ArrayList<ChangedFile> getChangedFiles() {
		return changedFiles;
	}

	public void addChangedFile(ChangedFile changedFile) {
		changedFiles.add(changedFile);
	}

}
