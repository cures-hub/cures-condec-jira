package de.uhd.ifi.se.decision.management.jira.extraction.decxtracteventlistener;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;

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

	@Test
	@NonTransactional
	public void testNoCommentContained() {
		Comment comment = createComment("");
		IssueEvent issueEvent = createIssueEvent(comment, EventType.ISSUE_DELETED_ID);
		listener.onIssueEvent(issueEvent);

		assertTrue(checkComment(""));
		DecisionKnowledgeElement element = getFirstElementInComment(comment);
		assertNull(element);
	}

	@Test
	@NonTransactional
	public void testRationaleTag() {
		Comment comment = createComment("{issue}This is a very severe issue.{issue}");
		IssueEvent issueEvent = createIssueEvent(comment, EventType.ISSUE_DELETED_ID);
		listener.onIssueEvent(issueEvent);

		// TODO Is this the correct behavior? Do we also want that the comment is
		// deleted?
		assertTrue(checkComment("{issue}This is a very severe issue.{issue}"));
		DecisionKnowledgeElement element = getFirstElementInComment(comment);
		assertNull(element);
	}

	@Test
	@NonTransactional
	public void testExcludedTag() {
		Comment comment = createComment("{code}public static class{code}");
		IssueEvent issueEvent = createIssueEvent(comment, EventType.ISSUE_DELETED_ID);
		listener.onIssueEvent(issueEvent);

		// TODO Is this the correct behavior? Do we also want that the comment is
		// deleted?
		assertTrue(checkComment("{code}public static class{code}"));
		DecisionKnowledgeElement element = getFirstElementInComment(comment);
		assertNull(element);
	}

	@Test
	@NonTransactional
	public void testRationaleIcon() {
		Comment comment = createComment("(!)This is a very severe issue.");
		IssueEvent issueEvent = createIssueEvent(comment, EventType.ISSUE_DELETED_ID);
		listener.onIssueEvent(issueEvent);

		// TODO Is this the correct behavior? Do we also want that the comment is
		// deleted?
		assertTrue(checkComment("(!)This is a very severe issue."));
		DecisionKnowledgeElement element = getFirstElementInComment(comment);
		assertNull(element);
	}
}
