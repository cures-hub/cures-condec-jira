package de.uhd.ifi.se.decision.management.jira.git.codesummarizer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.git.CodeSummarizer;
import de.uhd.ifi.se.decision.management.jira.git.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.git.model.Diff;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;

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
		assertEquals("", summarizer.createSummary(JiraIssues.getJiraIssueByKey(""), 0));
	}

	@Test
	public void testJiraIssueExisting() {
		String summary = summarizer.createSummary(JiraIssues.getJiraIssueByKey("TEST-30"), -1);
		assertTrue(summary.startsWith("<table"));
		assertFalse(summary.contains("readMe"));
	}

	@Test
	public void testJiraIssueExistingUnformated() {
		summarizer.setFormatForComments(true);
		assertTrue(summarizer.createSummary(mockJiraIssueForGitTestsTangled, 0)
				.startsWith("The following classes were changed:"));
	}

	@Test
	public void testDiffNull() {
		assertEquals("", summarizer.createSummary((Diff) null));
	}

	@Test
	public void testDiffEmpty() {
		assertEquals("", summarizer.createSummary(new Diff()));
	}

	@Test
	public void testMinProbabilityOfCorrectnessZero() {
		assertFalse(summarizer.createSummary(JiraIssues.getJiraIssueByKey("TEST-4"), 0).contains("<td>"));
	}
}