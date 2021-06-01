package de.uhd.ifi.se.decision.management.jira.git.gitclient;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestDeleteRepositories extends TestSetUpGit {

	@Test
	public void testDeleteRepositoryNotNull() {
		assertTrue(gitClient.deleteRepositories());
		assertNotNull(gitClient);
	}

}