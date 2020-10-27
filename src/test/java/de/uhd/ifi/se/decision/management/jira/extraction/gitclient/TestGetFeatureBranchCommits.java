package de.uhd.ifi.se.decision.management.jira.extraction.gitclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;

public class TestGetFeatureBranchCommits extends TestSetUpGit {

	private String featureBranch = "featureBranch";

	@Test
	public void testGetRemoteBranches() {
		List<Ref> remoteBranches = gitClient.getAllRemoteBranches();
		assertEquals(3, remoteBranches.size());

		remoteBranches = gitClient.getGitClientsForSingleRepos().get(0).getRemoteBranches();
		assertEquals(3, remoteBranches.size());
	}

	@Test
	public void testGetMasterBranch() {
		Ref remoteBranch = gitClient.getBranch("master");
		assertEquals("refs/remotes/origin/master", remoteBranch.getName());
	}

	@Test
	public void testGetFeatureBranchCommitsByString() {
		List<RevCommit> commits = gitClient.getFeatureBranchCommits(featureBranch);
		assertNotNull(commits);
		assertEquals(4, commits.size());
	}

	@Test
	public void testGetFeatureBranchCommitsByRef() {
		// get the Ref
		List<Ref> remoteBranches = gitClient.getAllRemoteBranches();
		List<Ref> branchCandidates = remoteBranches.stream().filter(ref -> ref.getName().endsWith(featureBranch))
				.collect(Collectors.toList());

		assertEquals(1, branchCandidates.size());

		Ref featureBranch = branchCandidates.get(0);

		List<RevCommit> commits = gitClient.getFeatureBranchCommits(featureBranch);
		assertEquals(4, commits.size());
	}
}
