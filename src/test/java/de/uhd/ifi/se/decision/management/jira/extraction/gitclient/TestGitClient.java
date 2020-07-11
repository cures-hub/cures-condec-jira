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
		assertNotNull(gitClient.getGit(GIT_URI));
	}

	@Test
	public void testClonedRepoExisting() {
		assertTrue(gitClient.getDirectory(GIT_URI).exists());
	}

	@Test
	public void testGetRepositoryGitNull() {
		GitClient gitClient = new GitClient();
		assertNull(gitClient.getRepository(GIT_URI));
	}

	@Test
	public void testGetDirectoryGitNull() {
		GitClient gitClient = new GitClient();
		assertNull(gitClient.getDirectory(GIT_URI));
	}

	@Test
	public void testSetGit() {
		GitClient newGitClient = new GitClient(gitClient);
		newGitClient.setGit(gitClient.getGit(GIT_URI), GIT_URI);
		assertEquals(gitClient.getGit(GIT_URI), newGitClient.getGit(GIT_URI));
	}

	@Test
	public void testMockingOfGitDirectoryWorks() {
		assertEquals(GitClient.DEFAULT_DIR, System.getProperty("user.home") + File.separator + "data" + File.separator
				+ "condec-plugin" + File.separator + "git" + File.separator);
	}
}
