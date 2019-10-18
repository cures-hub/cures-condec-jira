package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.user.ApplicationUser;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;

import static org.junit.Assert.assertEquals;

import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import org.junit.Before;
import org.junit.Test;

public class TestCommitMessageToCommentTranscriber extends TestSetUp {

    private CommitMessageToCommentTranscriber transcriber;
    private ApplicationUser user;
    private Issue issue;

    @Before
    public void setUp() {
        init();
        issue = ComponentAccessor.getIssueManager().getIssueByCurrentKey("TEST-4");
        user = JiraUsers.SYS_ADMIN.getApplicationUser();
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

    @Test
    public void testPostComment() {
        String msg = "[issue]This is an issue![/Issue]";
        transcriber = new CommitMessageToCommentTranscriber(msg);
        transcriber.generateCommentString();
        transcriber.postComment(issue, user);
        assertEquals("{issue}This is an issue!{issue}",
                ComponentAccessor.getCommentManager().getComments(issue).get(0).getBody());
    }
}
