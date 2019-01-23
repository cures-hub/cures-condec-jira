package de.uhd.ifi.se.decision.management.jira.extraction.decxtracteventlistener;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
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

	@Test
	@NonTransactional
	public void testNoCommentContained() {
		Comment comment = createComment("");
		ComponentAccessor.getCommentManager().delete(comment);
		IssueEvent issueEvent = createIssueEvent(comment, EventType.ISSUE_DELETED_ID);
		listener.onIssueEvent(issueEvent);
		
		assertFalse(checkComment(""));
		DecisionKnowledgeElement element = getFirstElementInComment(comment);
		assertNull(element);
	}

	@Test
	@NonTransactional
	public void testRationaleTag() {
		Comment comment = createComment("{issue}This is a very severe issue.{issue}");
		ComponentAccessor.getCommentManager().delete(comment);
		IssueEvent issueEvent = createIssueEvent(comment, EventType.ISSUE_DELETED_ID);
		listener.onIssueEvent(issueEvent);
		
		assertFalse(checkComment("{issue}This is a very severe issue.{issue}"));
		DecisionKnowledgeElement element = getFirstElementInComment(comment);
		assertNull(element);
	}

	@Test
	@NonTransactional
	public void testExcludedTag() {
		Comment comment = createComment("{code}public static class{code}");
		ComponentAccessor.getCommentManager().delete(comment);
		IssueEvent issueEvent = createIssueEvent(comment, EventType.ISSUE_DELETED_ID);
		listener.onIssueEvent(issueEvent);

		assertFalse(checkComment("{code}public static class{code}"));
		DecisionKnowledgeElement element = getFirstElementInComment(comment);
		assertNull(element);
	}

	@Test
	@NonTransactional
	public void testRationaleIcon() {
		Comment comment = createComment("(!)This is a very severe issue.");
		ComponentAccessor.getCommentManager().delete(comment);
		IssueEvent issueEvent = createIssueEvent(comment, EventType.ISSUE_DELETED_ID);
		listener.onIssueEvent(issueEvent);

		assertFalse(checkComment("(!)This is a very severe issue."));
		DecisionKnowledgeElement element = getFirstElementInComment(comment);
		assertNull(element);
	}
}
