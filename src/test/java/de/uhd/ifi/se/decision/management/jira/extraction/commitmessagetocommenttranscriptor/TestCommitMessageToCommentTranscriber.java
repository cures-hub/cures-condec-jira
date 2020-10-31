package de.uhd.ifi.se.decision.management.jira.extraction.commitmessagetocommenttranscriptor;

import static org.junit.Assert.assertEquals;

import org.eclipse.jgit.lib.Ref;
import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;

import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.CommitMessageToCommentTranscriber;

public class TestCommitMessageToCommentTranscriber extends TestSetUpGit {

	private CommitMessageToCommentTranscriber transcriber;
	private Issue issue;
	private Ref branch;

	@Override
	@Before
	public void setUp() {
		super.setUp();
		String testIssueKey = "TEST-4";
		this.issue = ComponentAccessor.getIssueManager().getIssueByCurrentKey(testIssueKey);
		branch = gitClient.getBranches("TEST-4.transcriberBranch").get(0);
		this.transcriber = new CommitMessageToCommentTranscriber(issue);
	}

	@Test
	public void testPostCommits() {
		assertEquals(1, transcriber.postCommitsIntoJiraIssueComments().size());
	}

	@Test
	public void testPostDefaultBranchCommits() {
		assertEquals(0, transcriber.postDefaultBranchCommits().size());
	}

	@Test
	public void testPostFeatureBranchCommits() {
		assertEquals(1, transcriber.postFeatureBranchCommits().size());
	}

	// TODO: Test if duplicates are NOT postet.

}
