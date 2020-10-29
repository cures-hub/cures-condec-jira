package de.uhd.ifi.se.decision.management.jira.extraction.gitclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;

import com.atlassian.jira.issue.Issue;

import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;

public class TestGetCommits extends TestSetUpGit {

	@Test
	public void testBranch() {
		Ref featureBranch = gitClient.getBranches("featureBranch").get(0);
		List<RevCommit> commits = gitClient.getCommits(featureBranch);
		assertEquals(11, commits.size());
	}

	@Test
	public void testJiraIssue() {
		List<RevCommit> commits = gitClient.getCommits(mockJiraIssueForGitTests);
		assertEquals(2, commits.size()); // should be 3: two commits are on the default branch, one commit is on the
											// feature branch
		assertTrue(commits.get(0).getShortMessage().startsWith("TEST-12: Develop great software"));
	}

	@Test
	public void testJiraIssueNull() {
		List<RevCommit> commits = gitClient.getCommits((Issue) null);
		assertEquals(0, commits.size());
	}

	@Test
	public void testGitNull() {
		GitClient gitClient = new GitClient();
		List<RevCommit> commits = gitClient.getCommits(mockJiraIssueForGitTests);
		assertEquals(0, commits.size());
	}
}
