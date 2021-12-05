package de.uhd.ifi.se.decision.management.jira.git.gitclient;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.git.GitClientForSingleRepository;
import de.uhd.ifi.se.decision.management.jira.git.config.GitRepositoryConfiguration;

public class TestGitClientsForSingleRepos extends TestSetUpGit {

	@Test
	public void testGitObjectExisting() {
		assertNotNull(gitClient.getGitClientsForSingleRepo(GIT_URI).getGit());
		for (int i = 0; i < secureGitClients.size(); i++) {
			assertNotNull(secureGitClients.get(i).getGitClientsForSingleRepo(SECURE_GIT_URIS.get(i)).getGit());
		}
	}

	@Test
	public void testGetGitClientsForSingleRepoUriNotExisting() {
		assertNull(gitClient.getGitClientsForSingleRepo("dev0"));
	}

	@Test
	public void testInvalidRepoUri() {
		assertFalse(new GitClientForSingleRepository("TEST", new GitRepositoryConfiguration("", "", "", "", ""))
				.fetchOrClone());
	}

}
