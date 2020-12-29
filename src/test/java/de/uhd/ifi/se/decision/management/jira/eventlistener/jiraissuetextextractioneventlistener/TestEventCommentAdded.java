package de.uhd.ifi.se.decision.management.jira.eventlistener.jiraissuetextextractioneventlistener;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;

import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.issue.comments.Comment;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.PartOfJiraIssueText;
import net.java.ao.test.jdbc.NonTransactional;

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
		KnowledgeElement element = getFirstElementInComment(comment);
		assertNull(element);
	}

	@Test
	@NonTransactional
	public void testRationaleTag() {
		Comment comment = createCommentAndTestWhetherExistent("{issue}This is a very severe issue.{issue}");
		KnowledgeElement element = getFirstElementInComment(comment);
		assertEquals("This is a very severe issue.", element.getDescription());
		assertEquals(KnowledgeType.ISSUE, element.getType());
	}

	@Test
	@NonTransactional
	public void testJiraMacroTag() {
		Comment comment = createCommentAndTestWhetherExistent("{code:java}public static class{code}");
		PartOfJiraIssueText element = getFirstElementInComment(comment);
		assertEquals("{code:java}public static class{code}", element.getTextWithTags());
		assertEquals("public static class", element.getDescription());
		assertEquals(KnowledgeType.OTHER, element.getType());
	}

	@Test
	@NonTransactional
	public void testAnotherExcludedTag() {
		Comment comment = createCommentAndTestWhetherExistent("{color}green{color}");
		PartOfJiraIssueText element = getFirstElementInComment(comment);
		assertEquals("{color}green{color}", element.getTextWithTags());
		assertEquals("green", element.getDescription());
		assertEquals(KnowledgeType.OTHER, element.getType());
	}
}
