package de.uhd.ifi.se.decision.management.jira.persistence.jiraissuecommentpersistencemanager;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.extraction.TestTextSplitter;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueCommentPersistenceManager;
import net.java.ao.test.jdbc.NonTransactional;

public class TestGetJiraIssue extends TestJiraIssueCommentPersistenceManagerSetUp {

	@Test
	@NonTransactional
	public void testGetJiraIssueKeyForPartOfText() {
		List<PartOfJiraIssueText> comment = TestTextSplitter.getSentencesForCommentText(
				"some sentence in front. {issue} testobject {issue} some sentence in the back.");
		long id = JiraIssueCommentPersistenceManager.insertDecisionKnowledgeElement(comment.get(1), null);

		assertEquals(3, id);

		long jiraIssueId = comment.get(0).getJiraIssueId();

		List<DecisionKnowledgeElement> listWithObjects = JiraIssueCommentPersistenceManager
				.getElementsForIssue(jiraIssueId, "TEST");
		assertEquals(3, listWithObjects.size());

		String jiraIssueKey = JiraIssueCommentPersistenceManager.getJiraIssueKey(id);

		assertEquals("TEST-30", jiraIssueKey);
	}
}
