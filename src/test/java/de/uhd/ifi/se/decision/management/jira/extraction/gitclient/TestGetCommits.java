package de.uhd.ifi.se.decision.management.jira.extraction.gitclient;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;

public class TestGetCommits extends TestSetUpGit {

	@Test
	public void testRepositoryExisting() {
		GitClient gitClient = new GitClient("TEST");
		List<RevCommit> allCommits = gitClient.getCommits(GIT_URI);
		int expectedOnDefaultBranch = 8;
		int expectedOnFeatureBranch = 22; /* all = unique to the branch + parent branch's commits */
		int expectedAllCommitsNumber = expectedOnDefaultBranch + expectedOnFeatureBranch;
		assertEquals(expectedAllCommitsNumber, allCommits.size());
	}
}
