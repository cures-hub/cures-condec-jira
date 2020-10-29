package de.uhd.ifi.se.decision.management.jira.extraction.commitmessagetocommenttranscriptor;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;

import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.CommitMessageToCommentTranscriber;

public class TestGenerateCommentString extends TestSetUpGit {

	private CommitMessageToCommentTranscriber transcriber;
	private Issue issue;
	private Ref branch;

	private static String DEFAULT_EXPECTED_COMMENT_MESSAGE = "{issue}This is an issue!{issue}";
	private static String META_DATA_STRING = "\r\n\r\nAuthor: gitTest\r\n" + "Repository and Branch: " + GIT_URI + " "
			+ "refs/remotes/origin/TEST-4.transcriberBranch\r\n" + "Commit Hash: ";

	@Override
	@Before
	public void setUp() {
		super.setUp();
		String testIssueKey = "TEST-4";
		this.issue = ComponentAccessor.getIssueManager().getIssueByCurrentKey(testIssueKey);
		List<String> uris = new ArrayList<String>();
		uris.add(GIT_URI);
		branch = gitClient.getBranches("TEST-4.transcriberBranch").get(0);
		this.transcriber = new CommitMessageToCommentTranscriber(issue, gitClient);
	}

	@Test
	public void testCommitNull() {
		assertEquals("", transcriber.generateCommentString(null, branch));
	}

	@Test
	public void testBranchNull() {
		RevCommit commit = TestSetUpGit.gitClient.getFeatureBranchCommits(branch).get(0);
		assertEquals("", transcriber.generateCommentString(commit, null));
	}

	@Test
	public void testEmptyCommitMessage() {
		RevCommit commit = TestSetUpGit.gitClient.getFeatureBranchCommits(branch).get(4);
		assertEquals("", transcriber.generateCommentString(commit, branch));
	}

	@Test
	public void testLowercaseIssueMessage() {
		RevCommit commit = TestSetUpGit.gitClient.getFeatureBranchCommits(branch).get(3);
		assertEquals(DEFAULT_EXPECTED_COMMENT_MESSAGE + META_DATA_STRING + commit.getName(),
				transcriber.generateCommentString(commit, branch));
	}

	@Test
	public void testUppercaseIssueMessage() {
		RevCommit commit = TestSetUpGit.gitClient.getFeatureBranchCommits(branch).get(2);
		assertEquals(DEFAULT_EXPECTED_COMMENT_MESSAGE + META_DATA_STRING + commit.getName(),
				transcriber.generateCommentString(commit, branch));
	}

	@Test
	public void testMixedcaseIssueMessage() {
		RevCommit commit = TestSetUpGit.gitClient.getFeatureBranchCommits(branch).get(1);
		assertEquals(DEFAULT_EXPECTED_COMMENT_MESSAGE + META_DATA_STRING + commit.getName(),
				transcriber.generateCommentString(commit, branch));
	}

	@Test
	public void testIssueMessageWithAdditionalText() {
		RevCommit commit = TestSetUpGit.gitClient.getFeatureBranchCommits(branch).get(0);
		assertEquals(DEFAULT_EXPECTED_COMMENT_MESSAGE + " But I love pizza!" + META_DATA_STRING + commit.getName(),
				transcriber.generateCommentString(commit, branch));
	}
}
