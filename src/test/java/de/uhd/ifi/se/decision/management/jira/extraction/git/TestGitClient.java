package de.uhd.ifi.se.decision.management.jira.extraction.git;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;

public class TestGitClient extends TestSetUpGit {

	@Test
	public void testClonedRepoExisting() {
		File file = new File(uri);
		assertTrue(file.exists());
	}

	@Test
	public void getDiffNullRevCommit() {
		Map<DiffEntry, EditList> gitDiffs = gitClient.getDiff((RevCommit) null);
		assertEquals(gitDiffs, new HashMap<DiffEntry, EditList>());
	}

	@Test
	public void getDiffNullList() {
		Map<DiffEntry, EditList> gitDiffs = gitClient.getDiff((List<RevCommit>) null);
		assertTrue(gitDiffs == null);
	}

	@Test
	public void getDiffNullString() {
		Map<DiffEntry, EditList> gitDiffs = gitClient.getDiff((String) null);
		assertTrue(gitDiffs == null);
	}

	@Test
	public void getNoDiffsForNoCommits() {
		String commits = "{" + "\"commits\":[" + "" + "]" + "}";
		Map<DiffEntry, EditList> gitDiffs = gitClient.getDiff(commits);
		assertTrue(gitDiffs == null);
	}

	@Test
	public void getDiffsForCommits() {
		List<RevCommit> commits = gitClient.getCommits("TEST-12");
		Map<DiffEntry, EditList> gitDiffs = gitClient.getDiff(commits);
		assertEquals(0, gitDiffs.size(), 0.0);
	}
}
