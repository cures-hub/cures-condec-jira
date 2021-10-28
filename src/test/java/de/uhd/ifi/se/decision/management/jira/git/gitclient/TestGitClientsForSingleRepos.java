package de.uhd.ifi.se.decision.management.jira.git.gitclient;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class TestGitClientsForSingleRepos extends TestSetUpGit {

	@Test
	public void testGitObjectExisting() {
		assertNotNull(gitClient.getGitClientsForSingleRepo(GIT_URI).getGit());
		for (int i = 0; i < 3; i++) {
			assertNotNull(secureGitClients.get(i).getGitClientsForSingleRepo(SECURE_GIT_URIS.get(i)).getGit());
		}
	}

	@Test
	public void testGetGitClientsForSingleRepoUriNotExisting() {
		assertNull(gitClient.getGitClientsForSingleRepo("dev0"));
	}

}
