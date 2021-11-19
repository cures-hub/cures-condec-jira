package de.uhd.ifi.se.decision.management.jira.git.gitclient;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;
import org.junit.Test;

public class TestGetRefs extends TestSetUpGit {

	@Test
	public void testGetAllBranches() {
		List<Ref> remoteBranches = gitClient.getDiff("").getRefs();
		assertEquals(2, remoteBranches.size());

		remoteBranches = gitClient.getGitClientsForSingleRepos().get(0).getRefs();
		assertEquals(2, remoteBranches.size());
	}

	@Test
	public void testDefaultBranch() {
		assertEquals("refs/remotes/origin/master", gitClient.getDiff("master").get(0).getName());
	}

	@Test
	public void testFeatureBranch() {
		assertEquals("refs/remotes/origin/TEST-4.feature.branch", gitClient.getDiff("feature.branch").get(0).getName());
	}

	@Test
	public void testJiraIssueKey() {
		assertEquals("refs/remotes/origin/TEST-4.feature.branch", gitClient.getDiff("TEST-4").get(0).getName());
	}

	@Test
	public void testBranchNameInvalid() {
		assertEquals(0, gitClient.getDiff((String) null).size());
	}

	@Test
	public void testMasterBranchExists() {
		Git git = gitClient.getGitClientsForSingleRepo(GIT_URI).getGit();
		String currentBranch = null;
		try {
			currentBranch = git.getRepository().getBranch();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		assertEquals("master", currentBranch);
	}
}
