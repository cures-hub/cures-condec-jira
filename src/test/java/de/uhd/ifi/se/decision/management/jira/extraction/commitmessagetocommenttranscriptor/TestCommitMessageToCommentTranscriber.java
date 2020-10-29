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
		List<String> uris = new ArrayList<String>();
		uris.add(GIT_URI);
		branch = gitClient.getBranches("TEST-4.transcriberBranch").get(0);
		this.transcriber = new CommitMessageToCommentTranscriber(issue, gitClient);
	}

	@Test
	public void testEmptyCommitMessage() {
		RevCommit commit = TestSetUpGit.gitClient.getFeatureBranchCommits(branch).get(0);
		assertEquals("", transcriber.generateCommentString(commit, branch));
	}

	// TODO: Test if duplicates are NOT postet.

}
