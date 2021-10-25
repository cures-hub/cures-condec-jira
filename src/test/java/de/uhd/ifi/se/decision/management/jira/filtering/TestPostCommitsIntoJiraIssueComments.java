package de.uhd.ifi.se.decision.management.jira.filtering;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;

import de.uhd.ifi.se.decision.management.jira.git.CommitMessageToCommentTranscriber;
import de.uhd.ifi.se.decision.management.jira.git.config.GitConfiguration;
import de.uhd.ifi.se.decision.management.jira.git.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.mocks.MockDatabase;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.NonTransactional;

@Data(MockDatabase.class)
public class TestPostCommitsIntoJiraIssueComments extends TestSetUpGit {

	private CommitMessageToCommentTranscriber transcriber;

	@Before
	public void setUp() {
		super.setUp();
		GitConfiguration gitConfig = ConfigPersistenceManager.getGitConfiguration("TEST");
		gitConfig.setPostDefaultBranchCommitsActivated(true);
		gitConfig.setPostFeatureBranchCommitsActivated(true);
		ConfigPersistenceManager.saveGitConfiguration("TEST", gitConfig);

		Issue issue = ComponentAccessor.getIssueManager().getIssueByCurrentKey("TEST-4");
		ComponentAccessor.getCommentManager().deleteCommentsForIssue(issue);
		transcriber = new CommitMessageToCommentTranscriber(issue);
	}

	@Test
	@NonTransactional
	public void testPostCommits() {
		Ref featureBranch = gitClient.getBranches("TEST-4").get(0);
		List<RevCommit> commits = gitClient.getFeatureBranchCommits(featureBranch);
		assertEquals(5, commits.size());
		assertEquals(4, transcriber.postCommitsIntoJiraIssueComments().size());
	}

	@Test
	@NonTransactional
	public void testPostCommitsJiraIssueNull() {
		CommitMessageToCommentTranscriber transcriber = new CommitMessageToCommentTranscriber(null);
		assertEquals(0, transcriber.postCommitsIntoJiraIssueComments().size());
	}

	@Test
	@NonTransactional
	public void testPostDefaultBranchCommits() {
		assertEquals(0, transcriber.postDefaultBranchCommits().size());
	}

	@Test
	@NonTransactional
	public void testPostFeatureBranchCommits() {
		assertEquals(4, transcriber.postFeatureBranchCommits().size());
	}

	@Test
	@NonTransactional
	public void testPostCommitsAlreadyPosted() {
		assertEquals(4, transcriber.postCommitsIntoJiraIssueComments().size());
		assertEquals(0, transcriber.postCommitsIntoJiraIssueComments().size());
	}
}
