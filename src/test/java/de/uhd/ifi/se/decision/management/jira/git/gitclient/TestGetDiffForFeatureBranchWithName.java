package de.uhd.ifi.se.decision.management.jira.git.gitclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.git.model.Diff;

public class TestGetDiffForFeatureBranchWithName extends TestSetUpGit {

	@Test
	public void testFeatureBranch() {
		Diff diff = gitClient.getDiffForFeatureBranchWithName("TEST-4");

		assertEquals(1, diff.getRefs().size());
		Ref featureBranch = diff.getRefs().get(0);
		assertEquals("refs/remotes/origin/TEST-4.feature.branch", featureBranch.getName());
		assertEquals(GIT_URI, diff.get(0).getRepoUri());

		List<RevCommit> commits = diff.getCommits();
		assertEquals(5, commits.size());
		// oldest commits come first
		assertTrue(commits.get(0).getCommitTime() < commits.get(1).getCommitTime());
	}

	@Test
	public void testDefaultBranchCommitsAreNotIncluded() {
		Diff diff = gitClient.getDiffForFeatureBranchWithName("master");

		assertEquals(1, diff.getRefs().size());
		assertEquals("refs/remotes/origin/master", diff.getRefs().get(0).getName());

		List<RevCommit> commits = diff.getCommits();
		assertEquals(0, commits.size());
	}

	@Test
	public void testBranchNameNull() {
		List<RevCommit> commits = gitClient.getDiffForFeatureBranchWithName(null).getCommits();
		assertEquals(0, commits.size());
	}
}
