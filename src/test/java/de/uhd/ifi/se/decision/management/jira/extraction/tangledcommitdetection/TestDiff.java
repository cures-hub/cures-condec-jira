package de.uhd.ifi.se.decision.management.jira.extraction.tangledcommitdetection;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Map;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.EditList;
import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.extraction.Diff;
import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.ChangedFileImpl;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.DiffImpl;

public class TestDiff extends TestSetUpGit {

	private Diff diffsWithMoreThanOneCommits;
	private Diff diffsWithOneCommit;

	public Diff mapToDiff(GitClient gitClient, String jiraIssueKey) {
		DiffImpl mappedDiff = new DiffImpl();
		Map<DiffEntry, EditList> diff1 = gitClient.getDiff(mockJiraIssueForGitTests);
		for (Map.Entry<DiffEntry, EditList> entry : diff1.entrySet()) {
			File file = new File(gitClient.getDirectory().toString().replace(".git", "") + entry.getKey().getNewPath());
			mappedDiff.addChangedFile(new ChangedFileImpl(file));
		}
		return mappedDiff;
	}

	@Before
	public void setUp() {
		super.setUp();
		diffsWithMoreThanOneCommits = mapToDiff(gitClient, "TEST-66");
		diffsWithOneCommit = mapToDiff(gitClient, "TEST-77");

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