package de.uhd.ifi.se.decision.management.jira.persistence.jiraissuecommentpersistencemanager;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.model.Comment;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueCommentPersistenceManager;
import net.java.ao.test.jdbc.NonTransactional;

public class TestGetElementForIssue extends TestJiraIssueCommentPersistenceMangerSetUp {

	@Test
	@NonTransactional
	public void testGetElementsForIssue() {
		Comment comment = getComment("some sentence in front. {issue} testobject {issue} some sentence in the back.");
		long id = TestJiraIssueCommentPersistenceMangerSetUp.insertDecisionKnowledgeElement(comment,
				comment.getIssueId(), 1);

		assertEquals(3, id);

		List<DecisionKnowledgeElement> listWithObjects = JiraIssueCommentPersistenceManager
				.getElementsForIssue(comment.getIssueId(), "TEST");
		assertEquals(3, listWithObjects.size());
	}
}
