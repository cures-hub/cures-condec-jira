package de.uhd.ifi.se.decision.management.jira.git.gitclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;

public class TestGetDiffForBranchWithName extends TestSetUpGit {

	@Test
	public void testFeatureBranch() {
		List<RevCommit> commits = gitClient.getDiffForBranchWithName("TEST-4.feature.branch").getCommits();
		assertEquals(5, commits.size());
	}

	@Test
	public void testAllBranches() {
		List<RevCommit> commits = gitClient.getDiffForBranchWithName("master").getCommits();
		// TODO Why zero?
		assertEquals(0, commits.size());

		// oldest commits come first
		assertTrue(commits.get(0).getCommitTime() < commits.get(1).getCommitTime());
	}

	@Test
	public void testBranchNameNull() {
		List<RevCommit> commits = gitClient.getDiffForBranchWithName(null).getCommits();
		assertEquals(0, commits.size());
	}
}
