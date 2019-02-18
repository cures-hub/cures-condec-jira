package de.uhd.ifi.se.decision.management.jira.extraction.git;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.EditList;
import org.junit.AfterClass;
import org.junit.Test;


public class TestGitDiffExtraction extends TestSetUpGit {

	@Test
	public void getDiffNullList(){
		Map<DiffEntry, EditList> gitDiffs = gitClient.getDiff((List)null);
		assertTrue(gitDiffs == null);
	}

	@Test
	public void getDiffNullString(){
		Map<DiffEntry, EditList> gitDiffs = gitClient.getDiff((String) null);
		assertTrue(gitDiffs == null);
	}


	@Test
	public void getNoDiffsForNoCommits() {
		String commits = "{" + "\"commits\":[" + "" + "]" + "}";
		Map<DiffEntry, EditList> gitDiffs = gitClient.getDiff(commits);
		assertTrue(gitDiffs == null);

	}



	@AfterClass
	public static void tearDown() throws InterruptedException {
		Thread.sleep(2000);
	}
}
