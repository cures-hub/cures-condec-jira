package de.uhd.ifi.se.decision.management.jira.extraction.decxtracteventlistener;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.comments.MutableComment;

import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.NonTransactional;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TestSetUpWithIssues.AoSentenceTestDatabaseUpdater.class)
public class TestEventCommentEdited extends TestSetUpEventListener {
	@Test
	@NonTransactional
	public void testNoCommentContained() {
		Comment comment = createComment("");
		IssueEvent issueEvent = createIssueEvent(comment, EventType.ISSUE_COMMENT_EDITED_ID);
		listener.onIssueEvent(issueEvent);

		assertTrue(checkComment(""));
		DecisionKnowledgeElement element = getFirstElementInComment(comment);
		assertNull(element);
	}

	@Test
	@NonTransactional
	public void testRationaleTag() {
		Comment comment = createComment("{issue}This is a very severe issue.{issue}");
		((MutableComment) comment).setBody("{decision}This is the decision.{decision}");

		IssueEvent issueEvent = createIssueEvent(comment, EventType.ISSUE_COMMENT_EDITED_ID);
		listener.onIssueEvent(issueEvent);

		assertTrue(checkComment("{decision}This is the decision.{decision}"));
		DecisionKnowledgeElement element = getFirstElementInComment(comment);
		assertTrue(element.getDescription().equals("This is the decision."));
		assertTrue(element.getType() == KnowledgeType.DECISION);
	}

	@Test
	@NonTransactional
	public void testExcludedTag() {
		Comment comment = createComment("{code}public static class{code}");
		((MutableComment) comment).setBody("{noformat}This is a logger output.{notformat}");

		IssueEvent issueEvent = createIssueEvent(comment, EventType.ISSUE_COMMENT_EDITED_ID);
		listener.onIssueEvent(issueEvent);

		assertTrue(checkComment("{noformat}This is a logger output.{notformat}"));
		DecisionKnowledgeElement element = getFirstElementInComment(comment);
		assertTrue(element.getDescription().equals("{noformat}This is a logger output.{notformat}"));
		assertTrue(element.getType() == KnowledgeType.OTHER);
	}

	@Test
	@NonTransactional
	public void testRationaleIcon() {
		Comment comment = createComment("(!)This is a very severe issue.");
		((MutableComment) comment).setBody("(/)This is the decision.");

		IssueEvent issueEvent = createIssueEvent(comment, EventType.ISSUE_COMMENT_EDITED_ID);
		listener.onIssueEvent(issueEvent);

		assertTrue(checkComment("{decision}This is the decision.{decision}"));
		DecisionKnowledgeElement element = getFirstElementInComment(comment);
		assertTrue(element.getDescription().equals("This is the decision."));
		assertTrue(element.getType() == KnowledgeType.DECISION);
	}
}
