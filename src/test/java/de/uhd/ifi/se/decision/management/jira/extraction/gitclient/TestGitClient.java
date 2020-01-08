package de.uhd.ifi.se.decision.management.jira.extraction.gitclient;

import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.GitClientImpl;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class TestGitClient extends TestSetUpGit {

	@Test
	public void testGitObjectExisting() {
		assertNotNull(gitClient.getGit());
	}

	@Test
	public void testClonedRepoExisting() {
		assertTrue(gitClient.getDirectory().exists());
	}

	@Test
	public void testGetRepositoryGitNull() {
		GitClient gitClient = new GitClientImpl();
		assertNull(gitClient.getRepository());
	}

	@Test
	public void testGetDirectoryGitNull() {
		GitClient gitClient = new GitClientImpl();
		assertNull(gitClient.getDirectory());
	}

	@Test
	public void testSetGit() {
		GitClient newGitClient = new GitClientImpl();
		newGitClient.setGit(gitClient.getGit());
		assertEquals(gitClient.getGit(), newGitClient.getGit());
	}

	@Test
	public void testMockingOfGitDirectoryWorks() {
		assertEquals(GitClient.DEFAULT_DIR, System.getProperty("user.home") + File.separator + "data" + File.separator
				+ "condec-plugin" + File.separator + "git" + File.separator);
	}
}
