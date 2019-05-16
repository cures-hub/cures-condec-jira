package de.uhd.ifi.se.decision.management.jira.extraction.codesummarizer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.extraction.CodeSummarizer;
import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.CodeSummarizerImpl;

public class TestCodeSummarizer extends TestSetUpGit {

	private CodeSummarizer summarizer;

	@Before
	public void setUp() {
		super.setUp();
		summarizer = new CodeSummarizerImpl("TEST");
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
		assertEquals("", summarizer.createSummary("", 0 ));
	}


	@Test
	public void testRevCommitNull() {
		assertEquals("", summarizer.createSummary((RevCommit) null));
	}

	@Test
	public void testDiffNull() {
		assertEquals("", summarizer.createSummary((Map<DiffEntry, EditList>) null));
	}

	@Test
	public void testDiffEmpty() {
		assertEquals("", summarizer.createSummary(new HashMap<DiffEntry, EditList>()));
	}

}
