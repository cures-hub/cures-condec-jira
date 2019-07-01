package de.uhd.ifi.se.decision.management.jira.extraction.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.EditList;

import de.uhd.ifi.se.decision.management.jira.extraction.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.extraction.Diff;

/**
 * Class for a list of changed files. The scope for the diff might be a single
 * git commit, a whole feature branch (with many commits), or all commits
 * belonging to a JIRA issue.
 */
// TODO: Integrate Map<DiffEntry, EditList> diff into this class and
// change git client API to return objects of this class.
public class DiffImpl implements Diff {
	private List<ChangedFile> changedFiles;

	public DiffImpl() {
		this.changedFiles = new ArrayList<ChangedFile>();
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
