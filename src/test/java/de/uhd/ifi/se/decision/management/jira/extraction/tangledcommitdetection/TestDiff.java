package de.uhd.ifi.se.decision.management.jira.extraction.tangledcommitdetection;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.issue.Issue;

import de.uhd.ifi.se.decision.management.jira.extraction.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.extraction.Diff;
import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.DiffImpl;

public class TestDiff extends TestSetUpGit {

	private Diff diffForCommit;
	private Diff diffForJiraIssue;

	@Before
	public void setUp() {
		super.setUp();
		diffForCommit = createDiff(mockJiraIssueForGitTestsTangledSingleCommit);
		diffForJiraIssue = createDiff(mockJiraIssueForGitTestsTangled);
	}

	// TODO: Diff objects should be returned in the git client directly
	public static Diff createDiff(Issue jiraIssue) {
		List<RevCommit> commits = gitClient.getCommits(jiraIssue);
		Map<DiffEntry, EditList> diff = gitClient.getDiff(commits);
		String baseDirectory = gitClient.getDirectory().toString().replace(".git", "");
		return new DiffImpl(diff, baseDirectory);
	}

	@Test
	public void createDiff() {
		Diff diff = new DiffImpl();
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