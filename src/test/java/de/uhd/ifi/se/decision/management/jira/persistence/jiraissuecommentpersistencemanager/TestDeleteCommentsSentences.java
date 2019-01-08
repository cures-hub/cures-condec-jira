package de.uhd.ifi.se.decision.management.jira.persistence.jiraissuecommentpersistencemanager;

import net.java.ao.test.jdbc.NonTransactional;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestDeleteCommentsSentences extends TestJiraIssueCommentPersistenceMangerSetUp {

    @Test
    @NonTransactional
    public void testCommentNull(){
        manager.deleteCommentsSentences(null);
        assertTrue(true);
    }

    @Test
    @NonTransactional
    public void testCommentFilled(){
        addCommentsToIssue("This is a comment for test purposes");
        manager.deleteCommentsSentences(comment1);
        assertTrue(true);
    }
}
