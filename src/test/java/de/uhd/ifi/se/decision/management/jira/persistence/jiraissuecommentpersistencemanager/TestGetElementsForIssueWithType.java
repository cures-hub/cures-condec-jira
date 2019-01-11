package de.uhd.ifi.se.decision.management.jira.persistence.jiraissuecommentpersistencemanager;

import de.uhd.ifi.se.decision.management.jira.extraction.model.Comment;
import net.java.ao.test.jdbc.NonTransactional;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestGetElementsForIssueWithType extends TestJiraIssueCommentPersistenceMangerSetUp {

    @Test
    @NonTransactional
    public void testIdLessSProjectKeyNullTypeNull(){
        assertEquals(0, manager.getElementsForIssueWithType(-1, null,null).size());
    }

    @Test
    @NonTransactional
    public void testIdZeroSProjectKeyNullTypeNull(){
        assertEquals(0, manager.getElementsForIssueWithType(0, null,null).size());
    }

    @Test
    @NonTransactional
    public void testIdMoreSProjectKeyNullTypeNull(){
        assertEquals(0, manager.getElementsForIssueWithType(1, null,null).size());
    }

    @Test
    @NonTransactional
    public void testIdLessSProjectKeyFilledTypeNull(){
        assertEquals(0, manager.getElementsForIssueWithType(-1, "TEST",null).size());
    }

    @Test
    @NonTransactional
    public void testIdZeroSProjectKeyFilledTypeNull(){
        assertEquals(0, manager.getElementsForIssueWithType(0, "TEST",null).size());
    }

    @Test
    @NonTransactional
    public void testIdMoreSProjectKeyFilledTypeNull(){
        assertEquals(0, manager.getElementsForIssueWithType(1, "TEST",null).size());
    }


    @Test
    @NonTransactional
    public void testIdLessSProjectKeyNullTypeFilled(){
        assertEquals(0, manager.getElementsForIssueWithType(-1, null,"decision").size());
    }

    @Test
    @NonTransactional
    public void testIdZeroSProjectKeyNullTypeFilled(){
        assertEquals(0, manager.getElementsForIssueWithType(0, null,"decision").size());
    }

    @Test
    @NonTransactional
    public void testIdMoreSProjectKeyNullTypeFilled(){
        assertEquals(0, manager.getElementsForIssueWithType(1, null,"decision").size());
    }

    @Test
    @NonTransactional
    public void testIdLessSProjectKeyFilledTypeFilled(){
        assertEquals(0, manager.getElementsForIssueWithType(-1, "TEST","decision").size());
    }

    @Test
    @NonTransactional
    public void testIdZeroSProjectKeyFilledTypeFilled(){
        assertEquals(0, manager.getElementsForIssueWithType(0, "TEST","decision").size());
    }

    @Test
    @NonTransactional
    public void testIdMoreSProjectKeyFilledTypeFilled(){
        Comment comment = getComment("some sentence in front. {issue} testobject {issue} some sentence in the back.");
        TestJiraIssueCommentPersistenceMangerSetUp.insertDecisionKnowledgeElement(comment, comment.getIssueId(), 1);

        assertEquals(1, manager.getElementsForIssueWithType(comment.getIssueId(), "TEST","Issue").size());
    }
}
