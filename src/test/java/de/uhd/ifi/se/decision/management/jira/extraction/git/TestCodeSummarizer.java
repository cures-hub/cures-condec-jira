package de.uhd.ifi.se.decision.management.jira.extraction.git;

import de.uhd.ifi.se.decision.management.jira.extraction.CodeSummarizer;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.CodeSummarizerImpl;
import org.junit.Test;

public class TestCodeSummarizer extends TestSetUpGit{

	@Test
	public void testConstGit(){
		CodeSummarizer codeSummarizer = new CodeSummarizerImpl(gitClient, false);
		gitClient.deleteRepo();
	}
}
