package de.uhd.ifi.se.decision.management.jira.eventlistener.jiraissuetextextractioneventlistener;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.issue.comments.Comment;

import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.NonTransactional;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TestSetUpWithIssues.AoSentenceTestDatabaseUpdater.class)
public class TestEventCommentAdded extends TestSetUpEventListener {

	private Comment createCommentAndTestWhetherExistent(String inputBody, String outputBody) {
		Comment comment = createComment(inputBody);
		IssueEvent issueEvent = createIssueEvent(comment, EventType.ISSUE_COMMENTED_ID);
		listener.onIssueEvent(issueEvent);
		assertTrue(isCommentExistent(outputBody));
		return comment;
	}

	private Comment createCommentAndTestWhetherExistent(String commentBody) {
		return createCommentAndTestWhetherExistent(commentBody, commentBody);
	}

	@Test
	@NonTransactional
	public void testNoCommentContained() {
		Comment comment = createCommentAndTestWhetherExistent("");
		DecisionKnowledgeElement element = getFirstElementInComment(comment);
		assertNull(element);
	}

	@Test
	@NonTransactional
	public void testRationaleTag() {
		Comment comment = createCommentAndTestWhetherExistent("{issue}This is a very severe issue.{issue}");
		DecisionKnowledgeElement element = getFirstElementInComment(comment);
		assertTrue(element.getDescription().equals("This is a very severe issue."));
		assertTrue(element.getType() == KnowledgeType.ISSUE);
	}

	@Test
	@NonTransactional
	public void testExcludedTag() {
		Comment comment = createCommentAndTestWhetherExistent("{code}public static class{code}");
		DecisionKnowledgeElement element = getFirstElementInComment(comment);
		assertTrue(element.getDescription().equals("{code}public static class{code}"));
		assertTrue(element.getType() == KnowledgeType.OTHER);
	}

	@Test
	@NonTransactional
	public void testRationaleIcon() {
		Comment comment = createCommentAndTestWhetherExistent("(!)This is a very severe issue.",
				"{issue}This is a very severe issue.{issue}");
		DecisionKnowledgeElement element = getFirstElementInComment(comment);
		assertTrue(element.getDescription().equals("This is a very severe issue."));
		assertTrue(element.getType() == KnowledgeType.ISSUE);
	}
}
