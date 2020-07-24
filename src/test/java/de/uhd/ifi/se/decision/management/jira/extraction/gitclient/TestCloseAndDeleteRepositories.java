package de.uhd.ifi.se.decision.management.jira.extraction.gitclient;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class TestCloseAndDeleteRepositories extends TestSetUpGit {

	@Test
	public void testCloseGitExisting() {
		gitClient.closeAll();
		assertNotNull(gitClient);
	}

	@Test
	public void testDeleteRepositoryNotNull() {
		gitClient.deleteRepositories();
		assertNotNull(gitClient);
	}
}