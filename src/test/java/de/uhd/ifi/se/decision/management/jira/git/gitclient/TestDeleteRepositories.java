package de.uhd.ifi.se.decision.management.jira.git.gitclient;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.java.ao.test.jdbc.NonTransactional;

public class TestDeleteRepositories extends TestSetUpGit {

	@Test
	@NonTransactional
	public void testDeleteRepositoryNotNull() {
		assertTrue(gitClient.deleteRepositories());
		assertNotNull(gitClient);
		try {
			wait(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}