package de.uhd.ifi.se.decision.management.jira.git.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Diff extends ArrayList<DiffForSingleRepository> {

	private static final long serialVersionUID = 1L;

	public Diff() {
		super();
	}

	public Diff(DiffForSingleRepository diffForSingleRepository) {
		super();
		add(diffForSingleRepository);
	}

	/**
	 * @return files changed in the diff as a list of {@link ChangedFile} objects.
	 */
	public List<ChangedFile> getChangedFiles() {
		return stream().flatMap(diffForSingleRepo -> diffForSingleRepo.getChangedFiles().stream())
				.collect(Collectors.toList());
	}
}