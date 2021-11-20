package de.uhd.ifi.se.decision.management.jira.git.gitclient;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;

public class TestGetDiffOfEntireDefaultBranch extends TestSetUpGit {

	@Test
	public void testAllDefaultBranchCommits() {
		List<RevCommit> allCommits = gitClient.getDiffOfEntireDefaultBranch().getCommits();
		assertEquals(6, allCommits.size());
	}
}