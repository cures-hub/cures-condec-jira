package de.uhd.ifi.se.decision.management.jira.extraction.gitclient;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.List;
import org.eclipse.jgit.revwalk.RevCommit;

import com.atlassian.jira.issue.Issue;

public class TestGetNumberOfCommits extends TestSetUpGit {

	@Test
	public void testJiraIssueKeyEmptyString() {
		assertEquals(0, gitClient.getNumberOfCommits((Issue) null));
	}

	@Test
	public void testJiraIssueKeyExisting() {
		List<RevCommit> revcommits = gitClient.getCommits(mockJiraIssueForGitTests);
		for (RevCommit rc : revcommits) {
			System.out.println(rc.getFullMessage());
		}
		assertEquals(8, gitClient.getNumberOfCommits(mockJiraIssueForGitTests));
	}
}
