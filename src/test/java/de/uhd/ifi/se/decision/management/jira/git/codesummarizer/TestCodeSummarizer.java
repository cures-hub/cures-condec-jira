package de.uhd.ifi.se.decision.management.jira.git.codesummarizer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.git.CodeSummarizer;
import de.uhd.ifi.se.decision.management.jira.git.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.model.git.Diff;

public class TestCodeSummarizer extends TestSetUpGit {

	private CodeSummarizer summarizer;

	@Override
	@Before
	public void setUp() {
		super.setUp();
		summarizer = new CodeSummarizer(gitClient);
	}

	@Test
	public void testConstructorWithProjectKey() {
		CodeSummarizer codeSummarizer = new CodeSummarizer("TEST");
		assertNotNull(codeSummarizer);
	}

	@Test
	public void testJiraIssueKeyNull() {
		assertEquals("", summarizer.createSummary(null, 0));
	}

	@Test
	public void testJiraIssueKeyEmpty() {
		assertEquals("", summarizer.createSummary(null, 0));
	}

	@Test
	public void testJiraIssueExisting() {
		summarizer.setFormatForComments(false);
		assertTrue(summarizer.createSummary(mockJiraIssueForGitTestsTangled, 0).startsWith("<table"));
	}

	@Test
	public void testJiraIssueExistingUnformated() {
		summarizer.setFormatForComments(true);
		assertTrue(summarizer.createSummary(mockJiraIssueForGitTestsTangled, 0)
				.startsWith("The following classes were changed:"));
	}

	@Test
	public void testRevCommitNull() {
		assertEquals("", summarizer.createSummary((RevCommit) null));
	}

	@Test
	public void testRevCommitExisting() {
		List<RevCommit> commits = gitClient.getCommits(mockJiraIssueForGitTests);
		assertNotNull(summarizer.createSummary(commits.get(0)));
	}

	@Test
	public void testDiffNull() {
		assertEquals("", summarizer.createSummary((Diff) null));
	}

	@Test
	public void testDiffEmpty() {
		assertEquals("", summarizer.createSummary(new Diff()));
	}
}