package de.uhd.ifi.se.decision.management.jira.git;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.File;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;

public class TestGitRepositoryFileSystemManager extends TestSetUp {

	private GitRepositoryFileSystemManager fileSystemManager;

	@Before
	public void setUp() {
		init();
		fileSystemManager = new GitRepositoryFileSystemManager("CONDEC",
				"https://github.com/cures-hub/cures-condec-jira");
	}

	@Test
	public void testWorkingDirectoryExisting() {
		assertTrue(fileSystemManager.getPathToWorkingDirectory().exists());
	}

	@Test
	public void testDeleteWorkingDirectory() {
		assertTrue(fileSystemManager.deleteWorkingDirectory());
		assertFalse(fileSystemManager.deleteWorkingDirectory());
	}

	@Test
	public void testDeleteDirectoryNotExisting() {
		assertFalse(GitRepositoryFileSystemManager.deleteDirectory(new File("123-not-existing-file")));
	}

	@AfterClass
	public static void tearDown() {
		GitRepositoryFileSystemManager.deleteProjectDirectory("CONDEC");
	}

}
