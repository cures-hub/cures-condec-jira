package de.uhd.ifi.se.decision.management.jira.extraction.gitclient;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;

public class TestCloseAndDeleteRepositories extends TestSetUpGit {

	@Test
	public void testCloseGitNull() {
		GitClient gitClient = new GitClient();
		gitClient.closeAll();
		assertNotNull(gitClient);
	}

	@Test
	public void testCloseGitExisting() {
		gitClient.closeAll();
		assertNotNull(gitClient);
		setUpBeforeClass();
	}

	@Test
	public void testDeleteGitNull() {
		GitClient gitClient = new GitClient();
		gitClient.deleteRepositories();
		assertNotNull(gitClient);
	}

	@Test
	public void testDeleteRepositoryNotNull() {
		gitClient.deleteRepositories();
		assertNotNull(gitClient);
		setUpBeforeClass();
	}
}