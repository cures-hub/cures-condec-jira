package de.uhd.ifi.se.decision.management.jira.persistence.jiraissuecommentpersistencemanager;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.comments.MutableComment;
import de.uhd.ifi.se.decision.management.jira.extraction.model.Comment;
import de.uhd.ifi.se.decision.management.jira.extraction.model.Sentence;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueCommentPersistenceManager;
import net.java.ao.test.jdbc.NonTransactional;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;

public class TestGetDecisionKnowledgeElementLong extends TestJiraIssueCommentPersistenceMangerSetUp {

    @Test
    @NonTransactional
    public void testElementExistingInAo() {
        Comment comment = getComment("first Comment");
        long id = TestJiraIssueCommentPersistenceMangerSetUp.insertDecisionKnowledgeElement(comment, comment.getIssueId(), 0);
        assertNotNull(new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id));
    }

    @Test
    @NonTransactional
    public void testElementInsertedTwice() {
        Comment comment = getComment("first Comment");
        long id = TestJiraIssueCommentPersistenceMangerSetUp.insertDecisionKnowledgeElement(comment, comment.getIssueId(), 0);
        long id2 = TestJiraIssueCommentPersistenceMangerSetUp.insertDecisionKnowledgeElement(comment, comment.getIssueId(), 0);
        assertNotNull(new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id));
        assertTrue(id == id2);
    }

    @Test
    @NonTransactional
    public void testCommentHasChanged() {
        Comment comment = getComment("first Comment");
        long id = TestJiraIssueCommentPersistenceMangerSetUp.insertDecisionKnowledgeElement(comment, comment.getIssueId(), 0);

        Comment comment2 = getComment("second comment with more text");
        comment2.setJiraCommentId(comment.getJiraCommentId());

        Sentence element = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);

        // TODO @issue Is this the expected behaviour?
        assertNotNull(element);
    }

    @Test
    @NonTransactional
    public void testCleanSentenceDatabaseForProject() {
        Comment comment = getComment("some sentence in front.  {pro} testobject {pro} some sentence in the back.");
        long id = TestJiraIssueCommentPersistenceMangerSetUp.insertDecisionKnowledgeElement(comment, comment.getIssueId(), 1);

        MutableComment comment2 = ComponentAccessor.getCommentManager().getMutableComment(comment.getIssueId());
        ComponentAccessor.getCommentManager().delete(comment2);

        JiraIssueCommentPersistenceManager.cleanSentenceDatabaseForProject("TEST");

        Sentence element = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
        assertNull(element);
    }

    @Test
    @NonTransactional
    public void testSetRelevantIntoAO() {
        Comment comment = getComment("first Comment");
        long id = TestJiraIssueCommentPersistenceMangerSetUp.insertDecisionKnowledgeElement(comment, comment.getIssueId(), 0);

        Sentence element = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
        assertFalse(element.isRelevant());

        element.setRelevant(true);
        JiraIssueCommentPersistenceManager.updateInDatabase(element);
        element = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
        assertTrue(element.isRelevant());

        element.setRelevant(false);
        JiraIssueCommentPersistenceManager.updateInDatabase(element);
        element = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
        assertFalse(element.isRelevant());
    }

    @Test
    @NonTransactional
    public void testSetRelevantIntoAOForNonExistingElement() {
        Comment comment = getComment("first Comment");
        long id = TestJiraIssueCommentPersistenceMangerSetUp.insertDecisionKnowledgeElement(comment, comment.getIssueId(), 0);

        Sentence element = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
        element.setRelevant(true);
        element.setId(id + 2);

        assertFalse(JiraIssueCommentPersistenceManager.updateInDatabase(element));
    }
}
