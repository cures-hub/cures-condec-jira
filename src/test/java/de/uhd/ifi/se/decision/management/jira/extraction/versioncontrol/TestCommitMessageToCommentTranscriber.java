package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.Comment;

import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;

public class TestCommitMessageToCommentTranscriber extends TestSetUpGit {

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
	public void testEmptyMessage() {
		RevCommit commit = gitClient.getFeatureBranchCommits(issue).get(0);
		assertEquals("", transcriber.generateCommentString(commit, branch));
	}

	@Test
	public void testLowercaseIssueMessage() {
		RevCommit commit = TestSetUpGit.gitClient.getFeatureBranchCommits(this.branch).get(1);
		assertEquals(DEFAULT_EXPECTED_COMMENT_MESSAGE + META_DATA_STRING + commit.getName(),
				transcriber.generateCommentString(commit, branch));
	}

	@Test
	public void testUppercaseIssueMessage() {
		RevCommit commit = TestSetUpGit.gitClient.getFeatureBranchCommits(this.branch).get(2);
		assertEquals(DEFAULT_EXPECTED_COMMENT_MESSAGE + META_DATA_STRING + commit.getName(),
				transcriber.generateCommentString(commit, branch));
	}

	@Test
	public void testMixedcaseIssueMessage() {
		RevCommit commit = TestSetUpGit.gitClient.getFeatureBranchCommits(this.branch).get(3);
		assertEquals(DEFAULT_EXPECTED_COMMENT_MESSAGE + META_DATA_STRING + commit.getName(),
				transcriber.generateCommentString(commit, branch));
	}

	@Test
	public void testIssueMessageWithAdditionalText() {
		RevCommit commit = TestSetUpGit.gitClient.getFeatureBranchCommits(this.branch).get(4);
		assertEquals(DEFAULT_EXPECTED_COMMENT_MESSAGE + " But I love pizza!" + META_DATA_STRING + commit.getName(),
				transcriber.generateCommentString(commit, branch));
	}

	@Test
	public void testPostComment() {
		transcriber.postCommitsIntoJiraIssueComments();
		String additionalMessage = "";
		List<Comment> comments = ComponentAccessor.getCommentManager().getComments(issue);
		for (int i = 0, j = 1; i < comments.size(); i++, j++) {
			RevCommit currentCommit = TestSetUpGit.gitClient.getFeatureBranchCommits(this.branch).get(j);
			if (j == comments.size()) {
				additionalMessage = " But I love pizza!";
			}
			assertEquals(
					DEFAULT_EXPECTED_COMMENT_MESSAGE + additionalMessage + META_DATA_STRING + currentCommit.getName(),
					comments.get(i).getBody());
		}

	}

	// TODO: Test if duplicates are NOT postet.

}
