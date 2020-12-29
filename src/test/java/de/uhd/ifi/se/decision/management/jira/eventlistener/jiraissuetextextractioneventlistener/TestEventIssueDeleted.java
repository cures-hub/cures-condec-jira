package de.uhd.ifi.se.decision.management.jira.eventlistener.jiraissuetextextractioneventlistener;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.issue.comments.Comment;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import net.java.ao.test.jdbc.NonTransactional;

public class TestEventIssueDeleted extends TestSetUpEventListener {

	private boolean testIssueDeleteEvent(String commentBody) {
		Comment comment = createComment(commentBody);
		ComponentAccessor.getCommentManager().delete(comment);
		IssueEvent issueEvent = createIssueEvent(comment, EventType.ISSUE_DELETED_ID);
		listener.onIssueEvent(issueEvent);

		boolean isCommentDeleted = !isCommentExistent(commentBody);

		KnowledgeElement element = getFirstElementInComment(comment);
		boolean isElementDeletedInDatabase = (element == null);

		return isCommentDeleted && isElementDeletedInDatabase;
	}

	@Test
	@NonTransactional
	public void testNoCommentContained() {
		assertTrue(testIssueDeleteEvent(""));
	}

	@Test
	@NonTransactional
	public void testRationaleTag() {
		assertTrue(testIssueDeleteEvent("{issue}This is a very severe issue.{issue}"));
	}
}
