package de.uhd.ifi.se.decision.management.jira.extraction.gitclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;

import com.atlassian.jira.issue.Issue;

import de.uhd.ifi.se.decision.management.jira.model.git.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.model.git.Diff;

public class TestGetDiff extends TestSetUpGit {

	@Test
	public void testRevCommitNull() {
		Diff diff = gitClient.getDiff((RevCommit) null);
		assertEquals(0, diff.getChangedFiles().size());
	}

	@Test
	public void testRevLastCommitNull() {
		List<RevCommit> commits = gitClient.getCommits(mockJiraIssueForGitTests);
		assertFalse(commits.isEmpty());
		Diff diff = gitClient.getDiff(commits.get(0), (RevCommit) null);
		assertEquals(0, diff.getChangedFiles().size());
	}

	@Test
	public void testRevCommitExisting() {
		List<RevCommit> commits = gitClient.getCommits(mockJiraIssueForGitTests);
		assertFalse(commits.isEmpty());
		Diff diff = gitClient.getDiff(commits.get(0));
		assertEquals(1, diff.getChangedFiles().size());
		for (ChangedFile changedFile : diff.getChangedFiles()) {
			DiffEntry diffEntry = changedFile.getDiffEntry();
			assertEquals(ChangeType.ADD, diffEntry.getChangeType());

			EditList editList = changedFile.getEditList();
			assertEquals("EditList[INSERT(0-0,0-23)]", editList.toString());
		}
	}

	@Test
	public void testListOfRevCommitsNull() {
		Diff diff = gitClient.getDiff((List<RevCommit>) null);
		assertTrue(diff.getChangedFiles().isEmpty());
	}

	@Test
	public void testJiraIssueNull() {
		Diff diff = gitClient.getDiff((Issue) null);
		assertTrue(diff.getChangedFiles().isEmpty());
	}

	@Test
	public void testJiraIssueKeyExisting() {
		Diff diff = gitClient.getDiff(mockJiraIssueForGitTests);
		assertEquals(2, diff.getChangedFiles().size());

		List<ChangedFile> changedFiles = diff.getChangedFiles();

		assertTrue(changedFiles.get(0).getDiffEntry().toString()
				.contains("ADD GitDiffedCodeExtractionManager.REPLACE-PROBLEM.java"));
		assertEquals(1, changedFiles.get(0).getEditList().size());
		assertTrue(changedFiles.get(0).getEditList().toString().contains("INSERT(0-0,0-19)"));

		assertTrue(changedFiles.get(1).getDiffEntry().toString().contains("ADD GodClass.java"));
		assertEquals(1, changedFiles.get(1).getEditList().size());
		System.out.println(changedFiles.get(1).getEditList().toString());
		assertTrue(changedFiles.get(1).getEditList().toString().contains("INSERT(0-0,0-23)"));
	}

	@Test
	public void testDefaultBranchCommits() {
		Diff diff = gitClient.getDiffOfEntireDefaultBranch();
		assertEquals(6, diff.getChangedFiles().size());
		assertEquals(1, diff.getChangedFiles().get(2).getCommits().size());
		assertEquals("TEST-26", diff.getChangedFiles().get(2).getJiraIssueKeys().iterator().next());
	}

}
