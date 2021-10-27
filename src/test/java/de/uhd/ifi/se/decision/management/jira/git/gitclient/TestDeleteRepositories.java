package de.uhd.ifi.se.decision.management.jira.git.gitclient;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.git.GitClient;
import net.java.ao.test.jdbc.NonTransactional;

public class TestDeleteRepositories extends TestSetUpGit {

	@Test
	@NonTransactional
	public void testDeleteRepositoryNotNull() {
		assertTrue(GitClient.getInstance("TEST").deleteRepositories());
		assertNotNull(GitClient.getInstance("TEST"));
	}

}