package de.uhd.ifi.se.decision.management.jira.eventlistener.jiraissuetextextractioneventlistener;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.event.ProjectDeletedEvent;
import com.atlassian.jira.issue.comments.Comment;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraProjects;
import net.java.ao.test.jdbc.NonTransactional;

public class TestEventProjectDeleted extends TestSetUpEventListener {

	private boolean testProjectDeletedEvent(String commentBody) {
		Comment comment = createComment(commentBody);
		ComponentAccessor.getCommentManager().delete(comment);
		ProjectDeletedEvent projectDeletedEvent = new ProjectDeletedEvent(user, JiraProjects.getTestProject());
		listener.onProjectDeletedEvent(projectDeletedEvent);

		boolean isCommentDeleted = !isCommentExistent(commentBody);

		KnowledgeElement element = getFirstElementInComment(comment);
		boolean isElementDeletedInDatabase = (element == null);

		return isCommentDeleted && isElementDeletedInDatabase;
	}

	@Test
	@NonTransactional
	public void testNoCommentContained() {
		assertTrue(testProjectDeletedEvent(""));
	}

	@Test
	@NonTransactional
	public void testRationaleTag() {
		assertTrue(testProjectDeletedEvent("{issue}This is a very severe issue.{issue}"));
	}

	@Test
	@NonTransactional
	public void testExcludedTag() {
		assertTrue(testProjectDeletedEvent("{code}public static class{code}"));
	}

	@Test
	@NonTransactional
	public void testRationaleIcon() {
		assertTrue(testProjectDeletedEvent("(!)This is a very severe issue."));
	}
}
