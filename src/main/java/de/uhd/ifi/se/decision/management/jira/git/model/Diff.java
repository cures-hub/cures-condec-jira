package de.uhd.ifi.se.decision.management.jira.git.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;

/**
 * Represents a list of changes, which can be made on various git branches from
 * various git repositories (e.g. separate repository for frontend and backend
 * development). Extends {@link ArrayList}, thus, it provides common list
 * methods such as {@link List#add(Object)}.
 * 
 * @see DiffForSingleRef Representation of the changes made on a single branch
 *      of a specific git repo
 * @see ChangedFile
 */
public class Diff extends ArrayList<DiffForSingleRef> {

	private static final long serialVersionUID = -8514671139662593928L;

	/**
	 * Creates an empty {@link Diff} object.
	 */
	public Diff() {
		super();
	}

	/**
	 * Creates a {@link Diff} object that already contains the changes made on a
	 * specific branch.
	 * 
	 * @param diffForSingleRef
	 *            {@link DiffForSingleRef} with e.g. commits and
	 *            {@link ChangedFile}s of a specific branch.
	 */
	public Diff(DiffForSingleRef diffForSingleRef) {
		this();
		add(diffForSingleRef);
	}

	/**
	 * @return files changed in the diff as a list of {@link ChangedFile} objects.
	 */
	public List<ChangedFile> getChangedFiles() {
		return stream().flatMap(diffForSingleRepo -> diffForSingleRepo.getChangedFiles().stream())
				.collect(Collectors.toList());
	}

	public List<RevCommit> getCommits() {
		List<RevCommit> commits = stream().flatMap(diffForSingleRepo -> diffForSingleRepo.getCommits().stream())
				.collect(Collectors.toList());
		commits.sort(Comparator.comparingInt(RevCommit::getCommitTime));
		return commits;
	}

	public List<Ref> getRefs() {
		return stream().map(diffForSingleRepo -> diffForSingleRepo.getRef()).collect(Collectors.toList());
	}

}