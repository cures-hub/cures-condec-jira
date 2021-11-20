package de.uhd.ifi.se.decision.management.jira.git.gitclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.git.model.Diff;

public class TestGetDiffForBranchWithName extends TestSetUpGit {

	@Test
	public void testFeatureBranch() {
		Diff diff = gitClient.getDiffForBranchWithName("TEST-4.feature.branch");

		assertEquals(1, diff.getRefs().size());
		assertEquals("refs/remotes/origin/TEST-4.feature.branch", diff.getRefs().get(0).getName());

		List<RevCommit> commits = diff.getCommits();
		assertEquals(5, commits.size());
		// oldest commits come first
		assertTrue(commits.get(0).getCommitTime() < commits.get(1).getCommitTime());
	}

	@Test
	public void testAllBranches() {
		List<RevCommit> commits = gitClient.getDiffForBranchWithName("master").getCommits();
		// TODO Why zero?
		assertEquals(0, commits.size());

	}

	@Test
	public void testBranchNameNull() {
		List<RevCommit> commits = gitClient.getDiffForBranchWithName(null).getCommits();
		assertEquals(0, commits.size());
	}
}
