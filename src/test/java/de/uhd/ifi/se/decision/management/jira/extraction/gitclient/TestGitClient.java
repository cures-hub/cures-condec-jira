package de.uhd.ifi.se.decision.management.jira.extraction.gitclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;

public class TestGitClient extends TestSetUpGit {

	@Test
	public void testGitObjectExisting() {
		assertNotNull(gitClient.getGitClientsForSingleRepo(GIT_URI).getGit());
	}

	@Test
	public void testClonedRepoExisting() {
		assertTrue(gitClient.getGitClientsForSingleRepo(GIT_URI).getDirectory().exists());
	}

	@Test
	public void testGetGitNull() {
		GitClient gitClient = new GitClient();
		assertNull(gitClient.getGitClientsForSingleRepo(GIT_URI));
	}

	@Test
	public void testMockingOfGitDirectoryWorks() {
		assertEquals(GitClient.DEFAULT_DIR, System.getProperty("user.home") + File.separator + "data" + File.separator
				+ "condec-plugin" + File.separator + "git" + File.separator);
	}

	@Test
	public void testGetOrCreateProjectKeyInvalid() {
		assertNull(GitClient.getOrCreate(""));
		assertNull(GitClient.getOrCreate(null));
	}
}
