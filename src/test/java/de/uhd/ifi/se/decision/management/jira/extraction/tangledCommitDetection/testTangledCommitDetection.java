package de.uhd.ifi.se.decision.management.jira.extraction.tangledCommitDetection;

import de.uhd.ifi.se.decision.management.jira.extraction.CodeSummarizer;
import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;

import de.uhd.ifi.se.decision.management.jira.extraction.impl.CodeSummarizerImpl;
import org.junit.Before;

public class testTangledCommitDetection extends TestSetUpGit {

    private CodeSummarizer summarizer;


    @Before
    public void setUp() {
        super.setUp();
        summarizer = new CodeSummarizerImpl("TEST");
    }



}
