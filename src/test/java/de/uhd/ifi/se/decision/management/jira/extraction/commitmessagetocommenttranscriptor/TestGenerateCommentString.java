package de.uhd.ifi.se.decision.management.jira.extraction.commitmessagetocommenttranscriptor;

import static org.junit.Assert.assertEquals;

import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.component.ComponentAccessor;

import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.CommitMessageToCommentTranscriber;

public class TestGenerateCommentString extends TestSetUpGit {

	private CommitMessageToCommentTranscriber transcriber;
	private Ref branch;

	@Override
	@Before
	public void setUp() {
		super.setUp();
		branch = gitClient.getBranches("TEST-4.feature.branch").get(0);
		transcriber = new CommitMessageToCommentTranscriber(
				ComponentAccessor.getIssueManager().getIssueByCurrentKey("TEST-4"));
	}

	@Test
	public void testCommitNull() {
		assertEquals("", transcriber.generateCommentString(null, branch));
	}

	@Test
	public void testBranchNull() {
		RevCommit commit = TestSetUpGit.gitClient.getFeatureBranchCommits(branch).get(1);
		assertEquals("", transcriber.generateCommentString(commit, null));
	}

	@Test
	public void testEmptyCommitMessage() {
		RevCommit commit = TestSetUpGit.gitClient.getFeatureBranchCommits(branch).get(4);
		assertEquals("", transcriber.generateCommentString(commit, branch));
	}

	@Test
	public void testIssueMessageWithAdditionalText() {
		RevCommit commit = TestSetUpGit.gitClient.getFeatureBranchCommits(branch).get(5);
		String commitMetaData = "\r\n\r\nAuthor: gitTest\r\n" + "Repository and Branch: " + GIT_URI + " "
				+ "refs/remotes/origin/TEST-4.feature.branch\r\n" + "Commit Hash: ";

		assertEquals("{issue}This is an issue!{issue} But I love pizza!" + commitMetaData + commit.getName(),
				transcriber.generateCommentString(commit, branch));
	}
}
