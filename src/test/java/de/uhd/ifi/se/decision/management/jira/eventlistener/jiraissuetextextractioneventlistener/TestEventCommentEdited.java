package de.uhd.ifi.se.decision.management.jira.eventlistener.jiraissuetextextractioneventlistener;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.comments.MutableComment;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import net.java.ao.test.jdbc.NonTransactional;

public class TestEventCommentEdited extends TestSetUpEventListener {

    private Comment createAndChangeComment(String inputBody, String changedBody, String outputBody) {
        Comment comment = createComment(inputBody);
        ((MutableComment) comment).setBody(changedBody);

        IssueEvent issueEvent = createIssueEvent(comment, EventType.ISSUE_COMMENT_EDITED_ID);
        listener.onIssueEvent(issueEvent);

        assertTrue(isCommentExistent(outputBody));
        return comment;
    }

    private Comment createAndChangeComment(String commentBody, String changedBody) {
        return createAndChangeComment(commentBody, changedBody, changedBody);
    }

    @Test
    @NonTransactional
    public void testNoCommentContained() {
        Comment comment = createAndChangeComment("", "");
        DecisionKnowledgeElement element = getFirstElementInComment(comment);
        assertNull(element);
    }

    @Test
    @NonTransactional
    public void testRationaleTag() {
    	/* FIXME: @desombre ->
    	see below
    	 */
        Comment comment = createAndChangeComment("{issue}This is a very severe issue.{issue}",
                "{decision}This is the decision.{decision}");
        DecisionKnowledgeElement element = getFirstElementInComment(comment);
        assertTrue(element.getDescription().equals("This is the decision."));
        assertTrue(element.getType() == KnowledgeType.DECISION);
    }

    @Test
    @NonTransactional
    public void testExcludedTag() {
    	/* FIXME: @desombre ->
    	see below
    	 */
        Comment comment = createAndChangeComment("{code}public static class{code}",
                "{noformat}This is a logger output.{notformat}");
        DecisionKnowledgeElement element = getFirstElementInComment(comment);
        assertTrue(element.getDescription().equals("{noformat}This is a logger output.{notformat}"));
        assertTrue(element.getType() == KnowledgeType.OTHER);
    }

    @Test
    @NonTransactional
    public void testRationaleIcon() {
		/* FIXME: @desombre ->
		java.lang.IndexOutOfBoundsException: Index 0 out of bounds for length 0
	at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:64)
	at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:70)
	at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:248)
	at java.base/java.util.Objects.checkIndex(Objects.java:372)
	at java.base/java.util.ArrayList.set(ArrayList.java:472)
	at de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueTextPersistenceManager.updateComment(JiraIssueTextPersistenceManager.java:621)
	at de.uhd.ifi.se.decision.management.jira.eventlistener.JiraIssueTextExtractionEventListener.handleEditComment(JiraIssueTextExtractionEventListener.java:123)
	at de.uhd.ifi.se.decision.management.jira.eventlistener.JiraIssueTextExtractionEventListener.onIssueEvent(JiraIssueTextExtractionEventListener.java:58)
	at de.uhd.ifi.se.decision.management.jira.eventlistener.ConDecEventListener.onIssueEvent(ConDecEventListener.java:75)
	at de.uhd.ifi.se.decision.management.jira.eventlistener.jiraissuetextextractioneventlistener.TestEventCommentEdited.createAndChangeComment(TestEventCommentEdited.java:24)
	at de.uhd.ifi.se.decision.management.jira.eventlistener.jiraissuetextextractioneventlistener.TestEventCommentEdited.testRationaleIcon(TestEventCommentEdited.java:65)

		 */
        Comment comment = createAndChangeComment("(!)This is a very severe issue.", "(/)This is the decision.",
                "{decision}This is the decision.{decision}");
        DecisionKnowledgeElement element = getFirstElementInComment(comment);
        assertTrue(element.getDescription().equals("This is the decision."));
        assertTrue(element.getType() == KnowledgeType.DECISION);
    }
}
