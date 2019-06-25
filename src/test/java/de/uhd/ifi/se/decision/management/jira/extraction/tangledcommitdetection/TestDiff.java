package de.uhd.ifi.se.decision.management.jira.extraction.tangledcommitdetection;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.extraction.Diff;
import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.ChangedFileImpl;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.DiffImpl;

public class TestDiff extends TestSetUpGit {

	private Diff diffsWithMoreThanOneCommits;
	private Diff diffsWithOneCommit;
	private Map<DiffEntry, EditList> oneCommit;
	private Map<DiffEntry, EditList> moreThanOneCommits;

	public Diff mapToDiff(Map<DiffEntry, EditList> diff) {
		DiffImpl mappedDiff = new DiffImpl();
		for (Map.Entry<DiffEntry, EditList> entry : diff.entrySet()) {
			File file = new File(gitClient.getDirectory().toString().replace(".git", "") + entry.getKey().getNewPath());
			mappedDiff.addChangedFile(new ChangedFileImpl(file));
		}
		return mappedDiff;
	}

	@Before
	public void setUp() {
		super.setUp();
		List<RevCommit> commit = gitClient.getCommits(mockJiraIssueForGitTestsTangledSingleCommit);
		oneCommit = gitClient.getDiff(commit);
		diffsWithOneCommit = mapToDiff(oneCommit);

		List<RevCommit> commits = gitClient.getCommits(mockJiraIssueForGitTestsTangled);
		moreThanOneCommits = gitClient.getDiff(commits);
		diffsWithMoreThanOneCommits = mapToDiff(moreThanOneCommits);

	}

	@Test
	public void createDiff() {
		DiffImpl diff = new DiffImpl();
		assertEquals(0, diff.getChangedFiles().size());
	}

	@Test
	public void testGetChangedFileImplsWithMoreThanOneCommits() {
		assertEquals(3, diffsWithMoreThanOneCommits.getChangedFiles().size());
	}

	@Test
	public void testGetChangedFileImplsWithOneCommit() {
		assertEquals(1, diffsWithOneCommit.getChangedFiles().size());
	}

	@Test
	public void testAddChangedFileImpl() {
		diffsWithOneCommit.addChangedFile(null);
		assertEquals(2, diffsWithOneCommit.getChangedFiles().size());
	}

}