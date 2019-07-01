package de.uhd.ifi.se.decision.management.jira.extraction.gitclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;

import com.atlassian.jira.issue.Issue;

import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.GitClientImpl;

public class TestGetDiff extends TestSetUpGit {

	@Test
	public void testRevCommitNull() {
		Map<DiffEntry, EditList> diff = gitClient.getDiff((RevCommit) null);
		assertEquals(diff, new HashMap<DiffEntry, EditList>());
	}

	@Test
	public void testRevCommitExisting() {
		List<RevCommit> commits = gitClient.getCommits(mockJiraIssueForGitTests);
		Map<DiffEntry, EditList> diff = gitClient.getDiff(commits.get(0));
		assertEquals(1, diff.size());
		for (Map.Entry<DiffEntry, EditList> entry : diff.entrySet()) {
			DiffEntry diffEntry = entry.getKey();
			assertEquals("DiffEntry[ADD GodClass.java]", diffEntry.toString());
			assertEquals(ChangeType.ADD, diffEntry.getChangeType());

			EditList editList = entry.getValue();
			assertEquals("EditList[INSERT(0-0,0-2)]", editList.toString());
		}
	}

	@Test
	public void testListOfRevCommitsNull() {
		Map<DiffEntry, EditList> diff = gitClient.getDiff((List<RevCommit>) null);
		assertNull(diff);
	}

	@Test
	public void testJiraIssueNull() {
		Map<DiffEntry, EditList> diff = gitClient.getDiff((Issue) null);
		assertNull(diff);
	}

	@Test
	public void testJiraIssueKeyExisting() {
		Map<DiffEntry, EditList> diff = gitClient.getDiff(mockJiraIssueForGitTests);
		assertEquals(2, diff.size());

		String diffEntries = diff.keySet().toString();
		assertTrue(diffEntries.contains("ADD GodClass.java"));
		assertTrue(diffEntries.contains("MODIFY readMe.txt"));

		String editLists = diff.values().toString();
		assertTrue(editLists.contains("INSERT(0-0,0-2)"));
		assertTrue(editLists.contains("REPLACE(0-1,0-1)"));
	}

	@Test
	public void testGitNull() {
		List<RevCommit> commits = gitClient.getCommits(mockJiraIssueForGitTests);

		GitClient newGitClient = new GitClientImpl();
		Map<DiffEntry, EditList> diff = newGitClient.getDiff(commits.get(0));
		assertEquals(0, diff.size());
		assertEquals(new HashMap<DiffEntry, EditList>(), diff);
	}
	
	@Test
	public void testMasterBranchExists() {
		Git git = gitClient.getGit();
		String currentBranch = null;
		try {
			currentBranch = git.getRepository().getBranch();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		assertEquals("master", currentBranch);
	}
}
