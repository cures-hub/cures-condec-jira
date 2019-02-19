package de.uhd.ifi.se.decision.management.jira.extraction.git;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import de.uhd.ifi.se.decision.management.jira.extraction.impl.GitClientImpl;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;


public class TestGitDiffExtraction extends TestSetUpGit {

	@Test
	public void getDiffNullList(){
		Map<DiffEntry, EditList> gitDiffs = gitClient.getDiff((List)null);
		assertTrue(gitDiffs == null);
		gitClient.deleteRepo();
	}

	@Test
	public void getDiffNullString() {
		Map<DiffEntry, EditList> gitDiffs = gitClient.getDiff((String) null);
		assertTrue(gitDiffs == null);
		gitClient.deleteRepo();
	}


	@Test
	public void getNoDiffsForNoCommits() {
		String commits = "{" + "\"commits\":[" + "" + "]" + "}";
		Map<DiffEntry, EditList> gitDiffs = gitClient.getDiff(commits);
		assertTrue(gitDiffs == null);
		gitClient.deleteRepo();
	}

	@Test
	public void getDiffsForCommits(){
		gitClient = new GitClientImpl(directory2, "TEST");
		List<RevCommit> commits = gitClient.getCommits("TEST-12");
		Map<DiffEntry, EditList> gitDiffs = gitClient.getDiff(commits);
		assertEquals(0, gitDiffs.size(), 0.0);
		gitClient.deleteRepo();
	}
}
