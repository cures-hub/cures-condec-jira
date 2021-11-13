package de.uhd.ifi.se.decision.management.jira.git.commitmessagetocommenttranscriber;

import static org.junit.Assert.assertEquals;

import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.component.ComponentAccessor;

import de.uhd.ifi.se.decision.management.jira.git.CommitMessageToCommentTranscriber;
import de.uhd.ifi.se.decision.management.jira.git.gitclient.TestSetUpGit;

public class TestGenerateCommentString extends TestSetUpGit {

	private CommitMessageToCommentTranscriber transcriber;
	private Ref branch;

	@Override
	@Before
	public void setUp() {
		super.setUp();
		branch = gitClient.getRefs("TEST-4.feature.branch").get(0);
		transcriber = new CommitMessageToCommentTranscriber(
				ComponentAccessor.getIssueManager().getIssueByCurrentKey("TEST-4"));
	}

	@Test
	public void testCommitNull() {
		assertEquals("", transcriber.generateCommentString(null, branch, GIT_URI));
	}

	@Test
	public void testBranchNull() {
		RevCommit commit = TestSetUpGit.gitClient.getFeatureBranchCommits(branch).get(1);
		assertEquals("", transcriber.generateCommentString(commit, null, GIT_URI));
	}

	@Test
	public void testEmptyCommitMessage() {
		RevCommit commit = TestSetUpGit.gitClient.getFeatureBranchCommits(branch).get(3);
		assertEquals("", transcriber.generateCommentString(commit, branch, GIT_URI));
	}

	@Test
	public void testIssueMessageWithAdditionalText() {
		RevCommit commit = TestSetUpGit.gitClient.getFeatureBranchCommits(branch).get(4);
		String commitMetaData = "\r\n\r\nAuthor: gitTest\r\n" + "Repository and Branch: " + GIT_URI + " "
				+ "refs/remotes/origin/TEST-4.feature.branch\r\n" + "Commit Hash: ";

		assertEquals("{issue}This is an issue!{issue} But I love pizza!" + commitMetaData + commit.getName(),
				transcriber.generateCommentString(commit, branch, GIT_URI));
	}
}
