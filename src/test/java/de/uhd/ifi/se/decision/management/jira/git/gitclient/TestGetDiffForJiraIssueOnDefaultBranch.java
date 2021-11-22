package de.uhd.ifi.se.decision.management.jira.git.gitclient;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;

import com.atlassian.jira.issue.Issue;

import de.uhd.ifi.se.decision.management.jira.git.model.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.git.model.Diff;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;

public class TestGetDiffForJiraIssueOnDefaultBranch extends TestSetUpGit {

	@Test
	public void testJiraIssueWithCommits() {
		Issue issue = JiraIssues.getJiraIssueByKey("TEST-12");
		Diff diff = gitClient.getDiffForJiraIssueOnDefaultBranch(issue);
		List<RevCommit> commits = diff.getCommits();
		assertEquals(1, commits.size());

		List<ChangedFile> changedFiles = diff.getChangedFiles();
		assertEquals(1, changedFiles.size());

		ChangedFile changedFile = changedFiles.get(0);
		DiffEntry diffEntry = changedFile.getDiffEntry();
		assertEquals(ChangeType.ADD, diffEntry.getChangeType());
		assertEquals("DiffEntry[ADD GodClass.java]", changedFile.getDiffEntry().toString());

		EditList editList = changedFile.getEditList();
		assertEquals(1, editList.size());
		assertEquals("EditList[INSERT(0-0,0-25)]", editList.toString());
	}

	@Test
	public void testJiraIssueNull() {
		List<RevCommit> commits = gitClient.getDiffForJiraIssueOnDefaultBranch(null).getCommits();
		assertEquals(0, commits.size());
	}
}
