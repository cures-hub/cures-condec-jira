package de.uhd.ifi.se.decision.management.jira.git.gitclient;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.git.GitClientForSingleRepository;
import de.uhd.ifi.se.decision.management.jira.git.config.GitConfiguration;
import de.uhd.ifi.se.decision.management.jira.git.config.GitRepositoryConfiguration;
import de.uhd.ifi.se.decision.management.jira.git.model.FileType;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;

public class TestGitClientsForSingleRepos extends TestSetUpGit {

	@Before
	public void setUp() {
		super.setUp();
		super.setUpGitClientsSecure();
	}

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

	@Test
	public void testNoFileTypesConfigured() {
		GitConfiguration gitConfig = ConfigPersistenceManager.getGitConfiguration("TEST");
		gitConfig.setFileTypesToExtract(new ArrayList<>());
		ConfigPersistenceManager.saveGitConfiguration("TEST", gitConfig);
		assertNotNull(new GitClientForSingleRepository("TEST",
				new GitRepositoryConfiguration(GIT_URI, "master", "NONE", "", "")));
	}

	@Test
	public void testTwoFileTypesConfigured() {
		GitConfiguration gitConfig = ConfigPersistenceManager.getGitConfiguration("TEST");
		gitConfig.setFileTypesToExtract(List.of(FileType.java(), FileType.javascript()));
		ConfigPersistenceManager.saveGitConfiguration("TEST", gitConfig);
		assertNotNull(new GitClientForSingleRepository("TEST",
				new GitRepositoryConfiguration(GIT_URI, "master", "NONE", "", "")));
	}
}