package de.uhd.ifi.se.decision.management.jira.extraction.gitclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.GitRepositoryFileSystemManager;

public class TestGitClient extends TestSetUpGit {

	@Test
	public void testMockingOfGitDirectoryWorks() {
		assertEquals(GitRepositoryFileSystemManager.GIT_DIRECTORY, System.getProperty("user.home") + File.separator
				+ "data" + File.separator + "condec-plugin" + File.separator + "git" + File.separator);
	}

	@Test
	public void testGetOrCreateProjectKeyInvalid() {
		assertNull(GitClient.getInstance(""));
		assertNull(GitClient.getInstance(null));
	}
}
