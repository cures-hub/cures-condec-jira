package de.uhd.ifi.se.decision.management.jira.extraction.decxtracteventlistener;

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
public class TestEventIssueDeleted extends TestSetUpEventListener {

	private boolean testIssueDeleteEvent(String commentBody) {
		Comment comment = createComment(commentBody);
		ComponentAccessor.getCommentManager().delete(comment);
		IssueEvent issueEvent = createIssueEvent(comment, EventType.ISSUE_DELETED_ID);
		listener.onIssueEvent(issueEvent);

		boolean isCommentDeleted = !isCommentExistent(commentBody);

		DecisionKnowledgeElement element = getFirstElementInComment(comment);
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

	@Test
	@NonTransactional
	public void testExcludedTag() {
		assertTrue(testIssueDeleteEvent("{code}public static class{code}"));
	}

	@Test
	@NonTransactional
	public void testRationaleIcon() {
		assertTrue(testIssueDeleteEvent("(!)This is a very severe issue."));
	}
}
