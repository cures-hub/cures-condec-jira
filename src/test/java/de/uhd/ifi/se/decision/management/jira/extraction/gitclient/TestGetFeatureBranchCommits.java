package de.uhd.ifi.se.decision.management.jira.extraction.gitclient;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;

import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;

public class TestGetFeatureBranchCommits extends TestSetUpGit {

	@Test
	public void testGetFeatureBranchCommitsByRef() {
		Ref featureBranch = gitClient.getBranches("featureBranch").get(0);
		List<RevCommit> commits = gitClient.getFeatureBranchCommits(featureBranch);
		assertEquals(4, commits.size());
		assertFalse(commits.get(0).getCommitTime() < commits.get(1).getCommitTime());
	}

	@Test
	public void testGetFeatureBranchCommitsByRefNull() {
		List<RevCommit> commits = gitClient.getFeatureBranchCommits((Ref) null);
		assertEquals(0, commits.size());
	}

	@Test
	public void testGetFeatureBranchCommitsByJiraIssue() {
		Issue issue = ComponentAccessor.getIssueManager().getIssueByCurrentKey("TEST-4");
		assertEquals("TEST-4", issue.getKey());
		List<RevCommit> commits = gitClient.getFeatureBranchCommits(issue);
		assertEquals(5, commits.size());
	}

	@Test
	public void testGetFeatureBranchCommitsByJiraIssueNull() {
		List<RevCommit> commits = gitClient.getFeatureBranchCommits((Issue) null);
		assertEquals(0, commits.size());
	}
}
