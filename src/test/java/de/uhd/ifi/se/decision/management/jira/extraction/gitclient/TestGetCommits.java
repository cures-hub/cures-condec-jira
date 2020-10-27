package de.uhd.ifi.se.decision.management.jira.extraction.gitclient;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;

public class TestGetCommits extends TestSetUpGit {

	@Test
	public void testGetCommitsFromDefaultBranch() {
		List<RevCommit> allCommits = gitClient.getGitClientsForSingleRepos().get(0).getCommitsFromDefaultBranch();
		int expectedOnDefaultBranch = 7;
		// TODO Commits are doubled: why?
		for (RevCommit commit : allCommits) {
			System.out.println(commit.getShortMessage());
		}
		assertEquals(expectedOnDefaultBranch, allCommits.size());
	}
}
