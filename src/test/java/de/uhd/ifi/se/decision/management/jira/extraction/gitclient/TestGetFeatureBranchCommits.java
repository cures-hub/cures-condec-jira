package de.uhd.ifi.se.decision.management.jira.extraction.gitclient;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;

public class TestGetFeatureBranchCommits extends TestSetUpGit {

	@Test
	public void testGetRemoteBranches() {
		List<Ref> remoteBranches = gitClient.getAllRemoteBranches();
		assertEquals(3, remoteBranches.size());

		remoteBranches = gitClient.getGitClientsForSingleRepos().get(0).getRemoteBranches();
		assertEquals(3, remoteBranches.size());
	}

	@Test
	public void testGetFeatureBranchCommitsByRef() {
		Ref featureBranch = gitClient.getBranches("featureBranch").get(0);
		List<RevCommit> commits = gitClient.getFeatureBranchCommits(featureBranch);
		assertEquals(4, commits.size());
	}

	@Test
	public void testGetFeatureBranchCommitsByJiraIssue() {
		Issue issue = ComponentAccessor.getIssueManager().getIssueByCurrentKey("TEST-4");
		assertEquals("TEST-4", issue.getKey());
		List<RevCommit> commits = gitClient.getFeatureBranchCommits(issue);
		assertEquals(5, commits.size());
	}
}
