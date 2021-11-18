package de.uhd.ifi.se.decision.management.jira.git.model;

import java.util.ArrayList;
import java.util.List;

public class Diff extends ArrayList<DiffForSingleRepository> {

	private static final long serialVersionUID = -8514671139662593928L;

	public Diff() {
		super();
	}

	public Diff(DiffForSingleRepository diffForSingleRepository) {
		this();
		add(diffForSingleRepository);
	}

	/**
	 * @return files changed in the diff as a list of {@link ChangedFile} objects.
	 */
	public List<ChangedFile> getChangedFiles() {
		List<ChangedFile> allFiles = new ArrayList<>();
		for (DiffForSingleRepository diffForSingleRepository : this) {
			allFiles.addAll(diffForSingleRepository.getChangedFiles());
		}
		return allFiles;
	}
}