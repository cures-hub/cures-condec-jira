package de.uhd.ifi.se.decision.management.jira.extraction.gitclient;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;

public class TestCloseAndDeleteRepository extends TestSetUpGit {

    @Test
    public void testCloseGitNull() {
	GitClient gitClient = new GitClient();
	gitClient.closeAll();
	assertNotNull(gitClient);
    }

    @Test
    public void testCloseGitExisting() {
	gitClient.closeAll();
	assertNotNull(gitClient);
    }

    @Test
    public void testDeleteGitNull() {
	GitClient gitClient = new GitClient();
	gitClient.deleteRepository(GIT_URI);
	assertNotNull(gitClient);
    }

    @Test
    public void testDeleteRepositoryNotNull() {
	List<String> uris = new ArrayList<String>();
	uris.add(GIT_URI);
	GitClient gitClient = new GitClient(uris, "TEST");
	gitClient.deleteRepository(GIT_URI);
	assertNotNull(gitClient);
    }
}