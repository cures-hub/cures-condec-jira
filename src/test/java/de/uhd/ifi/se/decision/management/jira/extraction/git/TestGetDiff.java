package de.uhd.ifi.se.decision.management.jira.extraction.git;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;

public class TestGetDiff extends TestSetUpGit {

	@Test
	public void getDiffForRevCommitNull() {
		Map<DiffEntry, EditList> diff = gitClient.getDiff((RevCommit) null);
		assertEquals(diff, new HashMap<DiffEntry, EditList>());
	}

	@Test
	public void getDiffsForRevCommitExisting() {
		List<RevCommit> commits = gitClient.getCommits("TEST-12");
		Map<DiffEntry, EditList> diff = gitClient.getDiff(commits.get(0));
		assertEquals(0, diff.size());
		assertEquals(new HashMap<DiffEntry, EditList>(), diff);
	}

	@Test
	public void getDiffForListOfRevCommitsNull() {
		Map<DiffEntry, EditList> diff = gitClient.getDiff((List<RevCommit>) null);
		assertNull(diff);
	}

	@Test
	public void getDiffForJiraIssueKeyNull() {
		Map<DiffEntry, EditList> diff = gitClient.getDiff((String) null);
		assertNull(diff);
	}

	@Test
	public void getDiffForJiraIssueKeyNonExisting() {
		Map<DiffEntry, EditList> diff = gitClient.getDiff("");
		assertNull(diff);
	}

	@Test
	public void getDiffForJiraIssueKeyExisting() {
		Map<DiffEntry, EditList> diff = gitClient.getDiff("Test-12");
		assertEquals(0, diff.size());
		assertEquals(new HashMap<DiffEntry, EditList>(), diff);
	}
}
