package de.uhd.ifi.se.decision.management.jira.git.gitclient;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;

import com.atlassian.jira.issue.Issue;

import de.uhd.ifi.se.decision.management.jira.git.model.Diff;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;

public class TestGetDiffForJiraIssueOnDefaultBranchAndFeatureBranches extends TestSetUpGit {

	@Test
	public void testJiraIssueWithCommits() {
		Issue issue = JiraIssues.getJiraIssueByKey("TEST-4");
		Diff diff = gitClient.getDiffForJiraIssueOnDefaultBranchAndFeatureBranches(issue);

		assertEquals(1, diff.getRefs().size());
		assertEquals("refs/remotes/origin/TEST-4.feature.branch", diff.getRefs().get(0).getName());

		List<RevCommit> commits = diff.getCommits();
		assertEquals(5, commits.size());

		assertEquals(5, diff.getCodeElements().size());
		assertEquals(6, diff.getCommitElements().size());
	}

	@Test
	public void testJiraIssueNull() {
		List<RevCommit> commits = gitClient.getDiffForJiraIssueOnDefaultBranchAndFeatureBranches(null).getCommits();
		assertEquals(0, commits.size());
	}
}
