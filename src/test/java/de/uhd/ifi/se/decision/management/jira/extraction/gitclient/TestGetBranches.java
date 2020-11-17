package de.uhd.ifi.se.decision.management.jira.extraction.gitclient;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;
import org.junit.Before;
import org.junit.Test;

public class TestGetBranches extends TestSetUpGit {

	@Override
	@Before
	public void setUp() {
		super.setUp();
	}

	@Test
	public void testGetAllBranches() {
		List<Ref> remoteBranches = gitClient.getBranches();
		assertEquals(2, remoteBranches.size());

		remoteBranches = gitClient.getGitClientsForSingleRepos().get(0).getBranches();
		assertEquals(2, remoteBranches.size());
	}

	@Test
	public void testDefaultBranch() {
		assertEquals("refs/remotes/origin/master", gitClient.getBranches("master").get(0).getName());
	}

	@Test
	public void testFeatureBranch() {
		assertEquals("refs/remotes/origin/TEST-4.feature.branch",
				gitClient.getBranches("feature.branch").get(0).getName());
	}

	@Test
	public void testJiraIssueKey() {
		assertEquals("refs/remotes/origin/TEST-4.feature.branch", gitClient.getBranches("TEST-4").get(0).getName());
	}

	@Test
	public void testBranchNameInvalid() {
		assertEquals(0, gitClient.getBranches("").size());
		assertEquals(0, gitClient.getBranches(null).size());
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
