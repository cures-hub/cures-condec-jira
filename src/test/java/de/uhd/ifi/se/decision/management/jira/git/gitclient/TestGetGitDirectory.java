package de.uhd.ifi.se.decision.management.jira.git.gitclient;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestGetGitDirectory extends TestSetUpGit {

	@Test
	public void testClonedRepoExisting() {
		assertTrue(gitClient.getGitClientsForSingleRepo(GIT_URI).getGitDirectory().exists());
		for (int i = 0; i < 3; i++) {
			assertTrue(
					secureGitClients.get(i).getGitClientsForSingleRepo(SECURE_GIT_URIS.get(i)).getGitDirectory().exists());
		}
	}

	@Test
	public void testDirectoryIsGit() {
		assertTrue(gitClient.getGitClientsForSingleRepo(GIT_URI).getGitDirectory().toString().endsWith(".git"));
	}

}
