package de.uhd.ifi.se.decision.management.jira.eventlistener.jiraissuetextextractioneventlistener;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.issue.comments.Comment;

import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.NonTransactional;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TestSetUpWithIssues.AoSentenceTestDatabaseUpdater.class)
public class TestEventCommentDeleted extends TestSetUpEventListener {

	private boolean testCommentDeleteEvent(String commentBody) {
		Comment comment = createComment(commentBody);
		ComponentAccessor.getCommentManager().delete(comment);
		IssueEvent issueEvent = createIssueEvent(comment, EventType.ISSUE_COMMENT_DELETED_ID);
		listener.onIssueEvent(issueEvent);

		boolean isCommentDeleted = !isCommentExistent(commentBody);

		DecisionKnowledgeElement element = getFirstElementInComment(comment);
		boolean isElementDeletedInDatabase = (element == null);

		return isCommentDeleted && isElementDeletedInDatabase;
	}

	@Test
	@NonTransactional
	public void testNoCommentContained() {
		assertTrue(testCommentDeleteEvent(""));
	}

	@Test
	@NonTransactional
	public void testRationaleTag() {
		assertTrue(testCommentDeleteEvent("{issue}This is a very severe issue.{issue}"));
	}

	@Test
	@NonTransactional
	public void testExcludedTag() {
		assertTrue(testCommentDeleteEvent("{code}public static class{code}"));
	}

	@Test
	@NonTransactional
	public void testRationaleIcon() {
		assertTrue(testCommentDeleteEvent("(!)This is a very severe issue."));
	}
}
