package de.uhd.ifi.se.decision.management.jira.extraction.codesummarizer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.extraction.CodeSummarizer;
import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.CodeSummarizerImpl;
import de.uhd.ifi.se.decision.management.jira.model.git.Diff;
import de.uhd.ifi.se.decision.management.jira.model.git.impl.DiffImpl;

public class TestCodeSummarizer extends TestSetUpGit {

	private CodeSummarizer summarizer;

	@Before
	public void setUp() {
		super.setUp();
		summarizer = new CodeSummarizerImpl(gitClient);
	}

	@Test
	public void testConstructorWithProjectKey() {
		CodeSummarizer codeSummarizer = new CodeSummarizerImpl("TEST");
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
		assertTrue(summarizer.createSummary(mockJiraIssueForGitTestsTangled, 0).startsWith("<table"));
	}

	@Test
	public void testRevCommitNull() {
		assertEquals("", summarizer.createSummary((RevCommit) null));
	}

	@Test
	public void testDiffNull() {
		assertEquals("", summarizer.createSummary((Diff) null));
	}

	@Test
	public void testDiffEmpty() {
		assertEquals("", summarizer.createSummary(new DiffImpl()));
	}
	
	// TODO
	@Test
	public void testGetProjectKey() {
		assertEquals(null, ((CodeSummarizerImpl) summarizer).getProjectKey());
	}
	
	// TODO
	@Test
	public void testGetIssueKey() {
		assertEquals(null, ((CodeSummarizerImpl) summarizer).getIssueKey());
	}

}