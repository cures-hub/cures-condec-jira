package de.uhd.ifi.se.decision.management.jira.persistence.jiraissuecommentpersistencemanager;

import de.uhd.ifi.se.decision.management.jira.extraction.model.Comment;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.persistence.GenericLinkManager;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueCommentPersistenceManager;
import net.java.ao.test.jdbc.NonTransactional;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestCreateLinksForNonLinkedElementsForProject extends TestJiraIssueCommentPersistenceMangerSetUp {
    @Test
    @NonTransactional
    public void testLinkAllUnlikedSentence() {
        Comment comment = getComment("some sentence in front.  {pro} testobject {pro} some sentence in the back.");
        long id = TestJiraIssueCommentPersistenceMangerSetUp.insertDecisionKnowledgeElement(comment, comment.getIssueId(), 1);
        assertEquals(1, GenericLinkManager.getLinksForElement(id, DocumentationLocation.JIRAISSUECOMMENT).size());
        GenericLinkManager.deleteLinksForElement(id, DocumentationLocation.JIRAISSUECOMMENT);
        assertEquals(0, GenericLinkManager.getLinksForElement(id, DocumentationLocation.JIRAISSUECOMMENT).size());
        JiraIssueCommentPersistenceManager.createLinksForNonLinkedElementsForProject("TEST");
        assertEquals(1, GenericLinkManager.getLinksForElement(id, DocumentationLocation.JIRAISSUECOMMENT).size());
    }

    @Test
    @NonTransactional
    public void testProjectKeyNull(){
        JiraIssueCommentPersistenceManager.createLinksForNonLinkedElementsForProject(null);
    }

    @Test
    @NonTransactional
    public void testProjectKeyEmpty(){
        JiraIssueCommentPersistenceManager.createLinksForNonLinkedElementsForProject("");
    }
}
