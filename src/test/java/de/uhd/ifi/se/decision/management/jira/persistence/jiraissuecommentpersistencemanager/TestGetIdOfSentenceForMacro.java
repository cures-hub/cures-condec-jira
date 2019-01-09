package de.uhd.ifi.se.decision.management.jira.persistence.jiraissuecommentpersistencemanager;

import de.uhd.ifi.se.decision.management.jira.extraction.model.Comment;
import net.java.ao.test.jdbc.NonTransactional;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestGetIdOfSentenceForMacro extends TestJiraIssueCommentPersistenceMangerSetUp {

    @Test
    @NonTransactional
    public void testBodyNullIssueIdLessTypeNullKeyNull(){
        assertEquals(0, manager.getIdOfSentenceForMacro(null,-1,null, null));
    }

    @Test
    @NonTransactional
    public void testBodyFilledIssueIdLessTypeNullKeyNull(){
        assertEquals(0, manager.getIdOfSentenceForMacro("This is a comment for test purposes",-1,null, null));
    }

    @Test
    @NonTransactional
    public void testBodyNullIssueIdZeroTypeNullKeyNull(){
        assertEquals(0, manager.getIdOfSentenceForMacro(null,0,null, null));
    }

    @Test
    @NonTransactional
    public void testBodyFilledIssueIdZeroTypeNullKeyNull(){
        assertEquals(0, manager.getIdOfSentenceForMacro("This is a comment for test purposes",0,null, null));
    }

    @Test
    @NonTransactional
    public void testBodyNullIssueIdLessTypeFilledKeyNull(){
        assertEquals(0, manager.getIdOfSentenceForMacro(null,-1,"ISSUE", null));
    }

    @Test
    @NonTransactional
    public void testBodyFilledIssueIdLessTypeFilledKeyNull(){
        assertEquals(0, manager.getIdOfSentenceForMacro("This is a comment for test purposes",-1,"ISSUE", null));
    }

    @Test
    @NonTransactional
    public void testBodyNullIssueIdZeroTypeFilledKeyNull(){
        assertEquals(0, manager.getIdOfSentenceForMacro(null,0,"ISSUE", null));
    }

    @Test
    @NonTransactional
    public void testBodyFilledIssueIdZeroTypeFilledKeyNull(){
        assertEquals(0, manager.getIdOfSentenceForMacro("This is a comment for test purposes",0,"ISSUE", null));
    }

    //(fewfwf

    @Test
    @NonTransactional
    public void testBodyNullIssueIdLessTypeNullKeyFilled(){
        assertEquals(0, manager.getIdOfSentenceForMacro(null,-1,null, "TEST"));
    }

    @Test
    @NonTransactional
    public void testBodyFilledIssueIdLessTypeNullKeyFilled(){
        assertEquals(0, manager.getIdOfSentenceForMacro("This is a comment for test purposes",-1,null, "TEST"));
    }

    @Test
    @NonTransactional
    public void testBodyNullIssueIdZeroTypeNullKeyFilled(){
        assertEquals(0, manager.getIdOfSentenceForMacro(null,0,null, "TEST"));
    }

    @Test
    @NonTransactional
    public void testBodyFilledIssueIdZeroTypeNullKeyFilled(){
        assertEquals(0, manager.getIdOfSentenceForMacro("This is a comment for test purposes",0,null, "TEST"));
    }

    @Test
    @NonTransactional
    public void testBodyNullIssueIdLessTypeFilledKeyFilled(){
        assertEquals(0, manager.getIdOfSentenceForMacro(null,-1,"ISSUE", "TEST"));
    }

    @Test
    @NonTransactional
    public void testBodyFilledIssueIdLessTypeFilledKeyFilled(){
        assertEquals(0, manager.getIdOfSentenceForMacro("This is a comment for test purposes",-1,"ISSUE", "TEST"));
    }

    @Test
    @NonTransactional
    public void testBodyNullIssueIdZeroTypeFilledKeyFilled(){
        assertEquals(0, manager.getIdOfSentenceForMacro(null,0,"ISSUE", "TEST"));
    }

    @Test
    @NonTransactional
    public void testBodyFilledIssueIdZeroTypeFilledKeyFilled(){
        assertEquals(0, manager.getIdOfSentenceForMacro("This is a comment for test purposes",0,"ISSUE", "TEST"));
    }

    @Test
    @NonTransactional
    public void testBodyWrongIssueIdOkTypeFilledKeyFilled(){
        Comment comment = getComment("some sentence in front. {issue} testobject {issue} some sentence in the back.");
        TestJiraIssueCommentPersistenceMangerSetUp.insertDecisionKnowledgeElement(comment, comment.getIssueId(), 1);
        assertEquals(0,manager.getIdOfSentenceForMacro("Not the right Body",comment.getIssueId(),"Issue", "TEST"));
    }

    @Test
    @NonTransactional
    public void testBodyFilledIssueIdOkTypeFilledKeyFilled(){
        Comment comment = getComment("some sentence in front. {issue} testobject {issue} some sentence in the back.");
        TestJiraIssueCommentPersistenceMangerSetUp.insertDecisionKnowledgeElement(comment, comment.getIssueId(), 1);
        assertEquals(3,manager.getIdOfSentenceForMacro("testobject",comment.getIssueId(),"Issue", "TEST"));
    }
}
