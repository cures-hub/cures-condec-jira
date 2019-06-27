package de.uhd.ifi.se.decision.management.jira.extraction.impl;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.EditList;

import de.uhd.ifi.se.decision.management.jira.extraction.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.extraction.Diff;

public class DiffImpl implements Diff {
	private List<ChangedFile> changedFiles;

	public DiffImpl() {
		this.changedFiles = new LinkedList<ChangedFile>();
	}
	
	public DiffImpl(Map<DiffEntry, EditList> diff, String baseDirectory) {
		this();
		for (Map.Entry<DiffEntry, EditList> entry : diff.entrySet()) {
			File file = new File(baseDirectory + entry.getKey().getNewPath());
			this.addChangedFile(new ChangedFileImpl(file));
		}
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
