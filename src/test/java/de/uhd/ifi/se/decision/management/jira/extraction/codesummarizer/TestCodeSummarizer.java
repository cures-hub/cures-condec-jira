package de.uhd.ifi.se.decision.management.jira.extraction.git;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.extraction.CodeSummarizer;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.CodeSummarizerImpl;

public class TestCodeSummarizer extends TestSetUpGit {

	@Test
	public void testEmptyInput() {
		CodeSummarizer codeSummarizer = new CodeSummarizerImpl(gitClient, false);
		String summary = codeSummarizer.createSummary("");
		assertEquals(summary, "");
	}
}
