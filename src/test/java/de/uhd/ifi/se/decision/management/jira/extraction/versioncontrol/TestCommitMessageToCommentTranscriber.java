package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

public class TestCommitMessageToCommentTranscriber extends TestSetUp {

    private CommitMessageToCommentTranscriber transcriber;

    @Before
    public void setUp() {
        init();
    }

    @Test
    public void testEmptyMessage() {
        String msg = "";
        transcriber = new CommitMessageToCommentTranscriber(msg);
        assertEquals(msg, transcriber.generateCommentString());
    }

    @Test
    public void testLowercaseIssueMessage() {
        String msg = "[issue]This is an issue![/issue]";
        transcriber = new CommitMessageToCommentTranscriber(msg);
        assertEquals("{issue}This is an issue!{issue}", transcriber.generateCommentString());
    }
    @Test
    public void testUppercaseIssueMessage() {
        String msg = "[issue]This is an issue![/issue]";
        transcriber = new CommitMessageToCommentTranscriber(msg);
        assertEquals("{issue}This is an issue!{issue}", transcriber.generateCommentString());
    }
    @Test
    public void testMixedcaseIssueMessage() {
        String msg = "[issue]This is an issue![/Issue]";
        transcriber = new CommitMessageToCommentTranscriber(msg);
        assertEquals("{issue}This is an issue!{issue}", transcriber.generateCommentString());
    }
}
