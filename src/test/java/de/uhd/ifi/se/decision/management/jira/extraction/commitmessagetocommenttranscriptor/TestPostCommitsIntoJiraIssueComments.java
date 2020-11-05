package de.uhd.ifi.se.decision.management.jira.extraction.commitmessagetocommenttranscriptor;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;

import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.CommitMessageToCommentTranscriber;

public class TestPostCommitsIntoJiraIssueComments extends TestSetUpGit {

	private CommitMessageToCommentTranscriber transcriber;

	@Override
	@Before
	public void setUp() {
		super.setUp();
		Issue issue = ComponentAccessor.getIssueManager().getIssueByCurrentKey("TEST-4");
		this.transcriber = new CommitMessageToCommentTranscriber(issue);
	}

	@Test
	public void testPostCommits() {
		assertEquals(1, transcriber.postCommitsIntoJiraIssueComments().size());
	}

	@Test
	public void testPostCommitsJiraIssueNull() {
		CommitMessageToCommentTranscriber transcriber = new CommitMessageToCommentTranscriber(null);
		assertEquals(0, transcriber.postCommitsIntoJiraIssueComments().size());
	}

	@Test
	public void testPostDefaultBranchCommits() {
		assertEquals(0, transcriber.postDefaultBranchCommits().size());
	}

	@Test
	public void testPostFeatureBranchCommits() {
		assertEquals(1, transcriber.postFeatureBranchCommits().size());
	}

	@Test
	public void testPostCommitsAlreadyPosted() {
		assertEquals(1, transcriber.postCommitsIntoJiraIssueComments().size());
		assertEquals(0, transcriber.postCommitsIntoJiraIssueComments().size());
	}
}
