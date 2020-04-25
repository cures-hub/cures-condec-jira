package de.uhd.ifi.se.decision.management.jira.extraction.gitclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;

import com.atlassian.jira.issue.Issue;

import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.model.git.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.model.git.Diff;

public class TestGetDiff extends TestSetUpGit {

	@Test
	public void testRevCommitNull() {
		Diff diff = gitClient.getDiff((RevCommit) null, GIT_URI);
		assertEquals(0, diff.getChangedFiles().size());
	}

	@Test
	public void testRevCommitExisting() {
		List<RevCommit> commits = gitClient.getCommits(mockJiraIssueForGitTests, GIT_URI);
		Diff diff = gitClient.getDiff(commits.get(0), GIT_URI);
		assertEquals(1, diff.getChangedFiles().size());
		for (ChangedFile changedFile : diff.getChangedFiles()) {
			DiffEntry diffEntry = changedFile.getDiffEntry();
			assertEquals(ChangeType.ADD, diffEntry.getChangeType());

			EditList editList = changedFile.getEditList();
			assertEquals("EditList[INSERT(0-0,0-2)]", editList.toString());
		}
	}

	@Test
	public void testListOfRevCommitsNull() {
		Diff diff = gitClient.getDiff((List<RevCommit>) null, GIT_URI);
		assertNull(diff);
	}

	@Test
	public void testJiraIssueNull() {
		Diff diff = gitClient.getDiff((Issue) null, GIT_URI);
		assertNull(diff);
	}

	@Test
	public void testJiraIssueKeyExisting() {
		Diff diff = gitClient.getDiff(mockJiraIssueForGitTests, GIT_URI);
		assertEquals(2, diff.getChangedFiles().size());

		List<ChangedFile> changedFiles = diff.getChangedFiles();

		assertTrue(changedFiles.get(0).getDiffEntry().toString()
				.contains("ADD GitDiffedCodeExtractionManager.REPLACE-PROBLEM.java"));
		assertEquals(1, changedFiles.get(0).getEditList().size());
		assertTrue(changedFiles.get(0).getEditList().toString().contains("INSERT(0-0,0-58)"));

		assertTrue(changedFiles.get(1).getDiffEntry().toString().contains("ADD GodClass.java"));
		assertEquals(1, changedFiles.get(1).getEditList().size());
		assertTrue(changedFiles.get(1).getEditList().toString().contains("INSERT(0-0,0-2)"));
	}

	@Test
	public void testGitNull() {
		List<RevCommit> commits = gitClient.getCommits(mockJiraIssueForGitTests, GIT_URI);

		GitClient newGitClient = new GitClient();
		Diff diff = newGitClient.getDiff(commits.get(0), GIT_URI);
		assertEquals(0, diff.getChangedFiles().size());
	}

	@Test
	public void testMasterBranchExists() {
		Git git = gitClient.getGit(GIT_URI);
		String currentBranch = null;
		try {
			currentBranch = git.getRepository().getBranch();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		assertEquals("master", currentBranch);
	}
}
