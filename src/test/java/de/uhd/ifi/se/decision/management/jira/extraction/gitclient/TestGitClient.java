package de.uhd.ifi.se.decision.management.jira.extraction.gitclient;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Ignore;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;

@Ignore
public class TestGitClient extends TestSetUpGit {

	// @Test
	// public void testGitObjectExisting() {
	// assertNotNull(gitClient.getGit());
	// }
	//
	// @Test
	// public void testClonedRepoExisting() {
	// assertTrue(gitClient.getDirectory().exists());
	// }
	//
	// @Test
	// public void testGetRepositoryGitNull() {
	// GitClient gitClient = new GitClient();
	// assertNull(gitClient.getRepository());
	// }
	//
	// @Test
	// public void testGetDirectoryGitNull() {
	// GitClient gitClient = new GitClient();
	// assertNull(gitClient.getDirectory());
	// }

	@Test
	public void testMockingOfGitDirectoryWorks() {
		assertEquals(GitClient.DEFAULT_DIR, System.getProperty("user.home") + File.separator + "data" + File.separator
				+ "condec-plugin" + File.separator + "git" + File.separator);
	}
}
