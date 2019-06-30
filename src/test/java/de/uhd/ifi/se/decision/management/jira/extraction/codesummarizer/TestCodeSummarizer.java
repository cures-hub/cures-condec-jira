package de.uhd.ifi.se.decision.management.jira.extraction.codesummarizer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.issue.Issue;

import de.uhd.ifi.se.decision.management.jira.extraction.CodeSummarizer;
import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.CodeSummarizerImpl;

public class TestCodeSummarizer extends TestSetUpGit {

	private CodeSummarizer summarizer;

	@Before
	public void setUp() {
		super.setUp();
		summarizer = new CodeSummarizerImpl(gitClient, false);
	}

	@Test
	public void testConstructorWithProjectKeyAndHtmlBoolean() {
		CodeSummarizer codeSummarizer = new CodeSummarizerImpl("TEST", false);
		assertNotNull(codeSummarizer);
	}

	@Test
	public void testConstructorWithProjectKey() {
		CodeSummarizer codeSummarizer = new CodeSummarizerImpl("TEST");
		assertNotNull(codeSummarizer);
	}

	@Test
	public void testJiraIssueNull() {
		assertEquals("", summarizer.createSummary((Issue) null));
	}

	@Test
	public void testJiraIssueExisting() {
		assertEquals("The following classes were changed: *GodClass*\n",
				summarizer.createSummary(mockJiraIssueForGitTests));
	}

	@Test
	public void testRevCommitNull() {
		assertEquals("", summarizer.createSummary((RevCommit) null));
	}

	@Test
	public void testRevCommitFilled() {
		List<RevCommit> commits = gitClient.getCommits(mockJiraIssueForGitTests);
		assertEquals("The following classes were changed: *GodClass*\n", summarizer.createSummary(commits.get(0)));
	}

	@Test
	public void testDiffNull() {
		assertEquals("", summarizer.createSummary((Map<DiffEntry, EditList>) null));
	}

	@Test
	public void testDiffEmpty() {
		assertEquals("", summarizer.createSummary(new HashMap<DiffEntry, EditList>()));
	}

	@Test
	public void testDiffFilled() {
		List<RevCommit> commits = gitClient.getCommits(mockJiraIssueForGitTests);
		Map<DiffEntry, EditList> diff = gitClient.getDiff(commits.get(0));
		assertEquals("The following classes were changed: *GodClass*\n", summarizer.createSummary(diff));
	}
}
