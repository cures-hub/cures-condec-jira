package de.uhd.ifi.se.decision.management.jira.git.gitclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.diff.EditList;
import org.junit.Test;

import com.atlassian.jira.issue.Issue;

import de.uhd.ifi.se.decision.management.jira.git.model.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.git.model.Diff;

public class TestGetDiff extends TestSetUpGit {

	@Test
	public void testRevCommitExisting() {
		Diff diff = gitClient.getDiff(mockJiraIssueForGitTests);
		ChangedFile changedFile = diff.getChangedFiles().get(0);
		DiffEntry diffEntry = changedFile.getDiffEntry();
		assertEquals(ChangeType.ADD, diffEntry.getChangeType());

		EditList editList = changedFile.getEditList();
		assertEquals("EditList[INSERT(0-0,0-25)]", editList.toString());
	}

	@Test
	public void testNameEmpty() {
		Diff diff = gitClient.getDiff("");
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
		assertEquals(1, diff.getChangedFiles().size());

		List<ChangedFile> changedFiles = diff.getChangedFiles();

		assertEquals("DiffEntry[ADD GodClass.java]", changedFiles.get(0).getDiffEntry().toString());
		assertEquals(1, changedFiles.get(0).getEditList().size());
		assertEquals("EditList[INSERT(0-0,0-25)]", changedFiles.get(0).getEditList().toString());
	}

	@Test
	public void testDefaultBranchCommits() {
		Diff diff = gitClient.getDiffOfEntireDefaultBranch();
		assertEquals(5, diff.getChangedFiles().size());
		ChangedFile extractedClass = diff.getChangedFiles().get(2);
		assertEquals("Tangled2.java", extractedClass.getName());
		assertEquals(1, extractedClass.getCommits().size());
		assertEquals("TEST-30", extractedClass.getJiraIssueKeys().iterator().next());
	}
}