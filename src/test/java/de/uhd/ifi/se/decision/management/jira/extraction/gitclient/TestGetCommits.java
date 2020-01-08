package de.uhd.ifi.se.decision.management.jira.extraction.gitclient;

import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestGetCommits extends TestSetUpGit {

	@Test
	public void testRepositoryExisting() {
		List<RevCommit> allCommits = gitClient.getCommits();
		int expectedOnDefaultBranch = 8;
		int expectedOnFeatureBranch = 22; /* all = unique to the branch + parent branch's commits*/
		int expectedAllCommitsNumber = expectedOnDefaultBranch + expectedOnFeatureBranch;
		assertEquals(expectedAllCommitsNumber, allCommits.size());
	}
}
