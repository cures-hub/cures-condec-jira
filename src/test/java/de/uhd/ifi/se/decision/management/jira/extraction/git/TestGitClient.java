package de.uhd.ifi.se.decision.management.jira.extraction.git;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.GitClientImpl;

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
	public void testCloseGitNull() {
		GitClient gitClient = new GitClientImpl();
		gitClient.close();
		assertNotNull(gitClient);
	}

	@Test
	public void testCloseGitExisting() {
		gitClient.close();
		assertNotNull(gitClient);
	}
	
	@Test
	public void testDeleteGitNull() {
		GitClient gitClient = new GitClientImpl();
		gitClient.deleteRepository();
		assertNotNull(gitClient);
	}
	
	@Test
	public void testDeleteGitExisting() {
		GitClient newGitClient = new GitClientImpl();
		newGitClient.setGit(gitClient.getGit());
		gitClient.deleteRepository();
		assertNotNull(gitClient);
	}
}
