package de.uhd.ifi.se.decision.management.jira.git.model;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.git.gitclient.TestSetUpGit;

public class TestDiff extends TestSetUpGit {

	private Diff diffForCommit;
	private Diff diffForJiraIssue;

	@Override
	@Before
	public void setUp() {
		super.setUp();
		diffForCommit = gitClient.getDiffForJiraIssueOnDefaultBranchAndFeatureBranches(mockJiraIssueForGitTestsTangledSingleCommit);
		diffForJiraIssue = gitClient.getDiffForJiraIssueOnDefaultBranchAndFeatureBranches(mockJiraIssueForGitTestsTangled);
	}

	@Test
	public void createDiff() {
		DiffForSingleRef diff = new DiffForSingleRef();
		assertEquals(0, diff.getChangedFiles().size());
	}

	@Test
	public void testGetChangedFilesWithMoreThanOneCommit() {
		List<ChangedFile> changedFiles = diffForJiraIssue.getChangedFiles();
		assertEquals(3, changedFiles.size());
		assertEquals("Tangled1.java", changedFiles.get(0).getName());
		assertEquals("Untangled.java", changedFiles.get(1).getName());
		assertEquals("Untangled2.java", changedFiles.get(2).getName());
	}

	@Test
	public void testGetChangedFilesWithOneCommit() {
		assertEquals(1, diffForCommit.getChangedFiles().size());
	}

	@Test
	public void testAddChangedFile() {
		diffForCommit.get(0).addChangedFile(null);
		assertEquals(2, diffForCommit.getChangedFiles().size());
	}
}