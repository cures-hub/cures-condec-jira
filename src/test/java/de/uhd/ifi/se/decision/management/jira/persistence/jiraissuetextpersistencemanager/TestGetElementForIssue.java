package de.uhd.ifi.se.decision.management.jira.persistence.jiraissuetextpersistencemanager;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.extraction.TestTextSplitter;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueTextPersistenceManager;
import net.java.ao.test.jdbc.NonTransactional;

public class TestGetElementForIssue extends TestJiraIssueCommentPersistenceManagerSetUp {

	@Test
	@NonTransactional
	public void testGetElementsForIssue() {
		List<PartOfJiraIssueText> comment = TestTextSplitter.getSentencesForCommentText(
				"some sentence in front. {issue} testobject {issue} some sentence in the back.");
		long id = JiraIssueTextPersistenceManager.insertDecisionKnowledgeElement(comment.get(1), null);

		assertEquals(3, id);

		List<DecisionKnowledgeElement> listWithObjects = JiraIssueTextPersistenceManager
				.getElementsForIssue(comment.get(0).getJiraIssueId(), "TEST");
		assertEquals(3, listWithObjects.size());
	}
}
