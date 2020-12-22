package de.uhd.ifi.se.decision.management.jira.eventlistener.jiraissuetextextractioneventlistener;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.comments.MutableComment;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.PartOfJiraIssueText;
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
		KnowledgeElement element = getFirstElementInComment(comment);
		assertNull(element);
	}

	@Test
	@NonTransactional
	public void testRationaleTag() {
		Comment comment = createAndChangeComment("{issue}This is a very severe issue.{issue}",
				"{decision}This is the decision.{decision}");
		KnowledgeElement element = getFirstElementInComment(comment);
		assertTrue(element.getDescription().equals("This is the decision."));
		assertTrue(element.getType() == KnowledgeType.DECISION);
	}

	@Test
	@NonTransactional
	@Ignore
	public void testExcludedTag() {
		Comment comment = createAndChangeComment("{code}public static class{code}",
				"{noformat}This is a logger output.{notformat}");
		PartOfJiraIssueText element = getFirstElementInComment(comment);
		assertEquals("{code}public static class{code}", element.getTextWithTags());
		assertTrue(element.getType() == KnowledgeType.OTHER);
	}

	@Test
	@NonTransactional
	public void testRationaleIcon() {
		Comment comment = createAndChangeComment("(!)This is a very severe issue.", "(/)This is the decision.",
				"{decision}This is the decision.{decision}");
		KnowledgeElement element = getFirstElementInComment(comment);
		assertTrue(element.getDescription().equals("This is the decision."));
		assertTrue(element.getType() == KnowledgeType.DECISION);
	}
}
