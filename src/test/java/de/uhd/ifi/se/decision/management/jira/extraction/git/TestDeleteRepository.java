package de.uhd.ifi.se.decision.management.jira.extraction.git;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.GitClientImpl;

public class TestDeleteRepository extends TestSetUpGit {

	@Test
	public void testDeleteGitNull() {
		GitClient gitClient = new GitClientImpl();
		gitClient.deleteRepository();
		assertNotNull(gitClient);
	}

	@Test
	public void testDeleteGitExisting() {
		gitClient.deleteRepository();
		assertNotNull(gitClient);
	}
}