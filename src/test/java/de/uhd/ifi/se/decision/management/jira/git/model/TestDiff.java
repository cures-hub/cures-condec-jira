package de.uhd.ifi.se.decision.management.jira.git.model;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.issue.Issue;

import de.uhd.ifi.se.decision.management.jira.git.gitclient.TestSetUpGit;

public class TestDiff extends TestSetUpGit {

	private Diff diffForCommit;
	private Diff diffForJiraIssue;

	@Override
	@Before
	public void setUp() {
		super.setUp();
		diffForCommit = createDiff(mockJiraIssueForGitTestsTangledSingleCommit);
		diffForJiraIssue = createDiff(mockJiraIssueForGitTestsTangled);
	}

	public static Diff createDiff(Issue jiraIssue) {
		List<RevCommit> commits = gitClient.getCommits(jiraIssue);
		return gitClient.getDiff(commits);
	}

	@Test
	public void createDiff() {
		Diff diff = new Diff();
		assertEquals(0, diff.getChangedFiles().size());
	}

	@Test
	public void testGetChangedFilesWithMoreThanOneCommit() {
		List<ChangedFile> changedFiles = diffForJiraIssue.getChangedFiles();
		assertEquals(3, diffForJiraIssue.getChangedFiles().size());
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
		diffForCommit.addChangedFile(null);
		assertEquals(2, diffForCommit.getChangedFiles().size());
	}
}