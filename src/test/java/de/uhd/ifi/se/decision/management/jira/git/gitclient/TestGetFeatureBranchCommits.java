package de.uhd.ifi.se.decision.management.jira.git.gitclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;

public class TestGetFeatureBranchCommits extends TestSetUpGit {

	@Test
	public void testGetFeatureBranchCommitsByRef() {
		List<RevCommit> commits = gitClient.getDiff("TEST-4.feature.branch").getCommits();
		assertEquals(5, commits.size());

		// oldest commits come first
		assertTrue(commits.get(0).getCommitTime() < commits.get(1).getCommitTime());
	}

	@Test
	public void testGetFeatureBranchCommitsByJiraIssue() {
		Issue issue = ComponentAccessor.getIssueManager().getIssueByCurrentKey("TEST-4");
		assertEquals("TEST-4", issue.getKey());
		List<RevCommit> commits = gitClient.getDiffForJiraIssue(issue).get(0).getCommits();
		assertEquals(5, commits.size());
	}
}
