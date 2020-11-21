package de.uhd.ifi.se.decision.management.jira.extraction.gitclient;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

public class TestDeleteRepositories extends TestSetUpGit {

	@Test
	@Ignore
	public void testDeleteRepositoryNotNull() {
		assertTrue(gitClient.deleteRepositories());
		assertNotNull(gitClient);
	}

}