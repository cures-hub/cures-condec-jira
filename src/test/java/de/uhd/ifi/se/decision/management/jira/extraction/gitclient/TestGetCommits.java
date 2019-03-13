package de.uhd.ifi.se.decision.management.jira.extraction.gitclient;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;

public class TestGetCommits extends TestSetUpGit {

	@Test
	public void testRepositoryExisting() {
		List<RevCommit> commits = gitClient.getCommits();
		assertEquals(3, commits.size());
	}
}
