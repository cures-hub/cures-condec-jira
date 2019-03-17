package de.uhd.ifi.se.decision.management.jira.persistence.jiraissuecommentpersistencemanager;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.extraction.TestCommentSplitter;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfComment;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueCommentPersistenceManager;
import net.java.ao.test.jdbc.NonTransactional;

public class TestGetElementForIssue extends TestJiraIssueCommentPersistenceManagerSetUp {

	@Test
	@NonTransactional
	public void testGetElementsForIssue() {
		List<PartOfComment> comment = TestCommentSplitter.getSentencesForCommentText(
				"some sentence in front. {issue} testobject {issue} some sentence in the back.");
		long id = JiraIssueCommentPersistenceManager.insertDecisionKnowledgeElement(comment.get(1), null);

		assertEquals(3, id);

		List<DecisionKnowledgeElement> listWithObjects = JiraIssueCommentPersistenceManager
				.getElementsForIssue(comment.get(0).getJiraIssueId(), "TEST");
		assertEquals(3, listWithObjects.size());
	}
}
