package de.uhd.ifi.se.decision.management.jira.extraction.git;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.extraction.impl.GitClientImpl;

public class TestGitClient extends TestSetUpGit {

	@Test
	public void testNotCloningNotExistingRepo() throws InterruptedException {
		new GitClientImpl(directory0, projectKey0);
		Thread.sleep(2000);
		File file = new File(
				System.getProperty("user.home") + File.separator + "repository" + File.separator + projectKey0);
		assertFalse(file.exists());
	}

	@Test
	public void testCloneRepo() throws InterruptedException {
		Thread.sleep(2000);
		File file = new File(
				System.getProperty("user.home") + File.separator + "repository" + File.separator + projectKey1);
		assertTrue(file.exists());
		gitClient.deleteRepo();
	}

	@Test
	public void testUpdateClonedRepo() throws InterruptedException {
		Thread.sleep(2000);
		Thread.sleep(2000);
		File file = new File(
				System.getProperty("user.home") + File.separator + "repository" + File.separator + projectKey1);
		File file1 = new File(
				System.getProperty("user.home") + File.separator + "repository" + File.separator + projectKey1 + "1");
		File file2 = new File(
				System.getProperty("user.home") + File.separator + "repository" + File.separator + projectKey1 + "2");
		assertTrue(file.exists());
		assertFalse(file1.exists());
		assertFalse(file2.exists());
		gitClient.deleteRepo();
	}
}
