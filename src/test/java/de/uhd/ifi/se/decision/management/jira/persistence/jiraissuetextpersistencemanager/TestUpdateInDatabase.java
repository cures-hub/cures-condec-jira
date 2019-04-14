package de.uhd.ifi.se.decision.management.jira.persistence.jiraissuetextpersistencemanager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.extraction.TestTextSplitter;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueTextPersistenceManager;
import net.java.ao.test.jdbc.NonTransactional;

public class TestUpdateInDatabase extends TestJiraIssueCommentPersistenceManagerSetUp {

	@Test
	@NonTransactional
	public void testUpdateSentenceElement() {
		List<PartOfJiraIssueText> comment = TestTextSplitter.getSentencesForCommentText("first Comment");
		long id = JiraIssueTextPersistenceManager.insertDecisionKnowledgeElement(comment.get(0), null);
		PartOfJiraIssueText sentence = comment.get(0);
		sentence.setType("ALTERNATIVE");
		JiraIssueTextPersistenceManager.updateInDatabase(sentence);
		PartOfJiraIssueText element = (PartOfJiraIssueText) new JiraIssueTextPersistenceManager("").getDecisionKnowledgeElement(id);
		assertTrue(element.getTypeAsString().equalsIgnoreCase("ALTERNATIVE"));
	}

	@Test
	@NonTransactional
	public void testUpdateKnowledgeType() {
		List<PartOfJiraIssueText> comment = TestTextSplitter.getSentencesForCommentText("first Comment");
		long id = JiraIssueTextPersistenceManager.insertDecisionKnowledgeElement(comment.get(0), null);
		PartOfJiraIssueText sentence = comment.get(0);
		sentence.setType(KnowledgeType.ALTERNATIVE);
		JiraIssueTextPersistenceManager.updateInDatabase(sentence);
		PartOfJiraIssueText element = (PartOfJiraIssueText) new JiraIssueTextPersistenceManager("").getDecisionKnowledgeElement(id);
		assertTrue(element.getTypeAsString().equalsIgnoreCase("ALTERNATIVE"));
	}

	@Test
	@NonTransactional
	public void testSetSentenceIrrlevant() {
		List<PartOfJiraIssueText> comment = TestTextSplitter.getSentencesForCommentText("first Comment");
		long id = JiraIssueTextPersistenceManager.insertDecisionKnowledgeElement(comment.get(0), null);
		PartOfJiraIssueText element = (PartOfJiraIssueText) new JiraIssueTextPersistenceManager("").getDecisionKnowledgeElement(id);
		element.setRelevant(true);

		JiraIssueTextPersistenceManager.updateInDatabase(element);
		element = (PartOfJiraIssueText) new JiraIssueTextPersistenceManager("").getDecisionKnowledgeElement(id);
		assertTrue(element.isRelevant());

		element.setRelevant(false);
		element.setValidated(true);
		JiraIssueTextPersistenceManager.updateInDatabase(element);
		element = (PartOfJiraIssueText) new JiraIssueTextPersistenceManager("").getDecisionKnowledgeElement(id);
		assertFalse(element.isRelevant());
		assertTrue(element.isValidated());
		assertTrue(element.getTypeAsString().equalsIgnoreCase("Other"));
	}
}
