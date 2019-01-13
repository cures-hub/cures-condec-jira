package de.uhd.ifi.se.decision.management.jira.persistence.jiraissuecommentpersistencemanager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.extraction.model.TestComment;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Sentence;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueCommentPersistenceManager;
import net.java.ao.test.jdbc.NonTransactional;

public class TestUpdateInDatabase extends TestJiraIssueCommentPersistenceManagerSetUp {

	@Test
	@NonTransactional
	public void testUpdateSentenceElement() {
		TestComment tc = new TestComment();
		List<Sentence> comment = tc.getSentencesForCommentText("first Comment");
		long id = JiraIssueCommentPersistenceManager.insertDecisionKnowledgeElement(comment.get(0), null);
		Sentence sentence = comment.get(0);
		sentence.setType("ALTERNATIVE");
		JiraIssueCommentPersistenceManager.updateInDatabase(sentence);
		Sentence element = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		assertTrue(element.getTypeAsString().equalsIgnoreCase("ALTERNATIVE"));
	}

	@Test
	@NonTransactional
	public void testUpdateKnowledgeType() {
		TestComment tc = new TestComment();
		List<Sentence> comment = tc.getSentencesForCommentText("first Comment");
		long id = JiraIssueCommentPersistenceManager.insertDecisionKnowledgeElement(comment.get(0), null);
		Sentence sentence = comment.get(0);
		sentence.setType(KnowledgeType.ALTERNATIVE);
		JiraIssueCommentPersistenceManager.updateInDatabase(sentence);
		Sentence element = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		assertTrue(element.getTypeAsString().equalsIgnoreCase("ALTERNATIVE"));
	}

	@Test
	@NonTransactional
	public void testSetSentenceIrrlevant() {
		TestComment tc = new TestComment();
		List<Sentence> comment = tc.getSentencesForCommentText("first Comment");
		long id = JiraIssueCommentPersistenceManager.insertDecisionKnowledgeElement(comment.get(0), null);
		Sentence element = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		element.setRelevant(true);

		JiraIssueCommentPersistenceManager.updateInDatabase(element);
		element = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		assertTrue(element.isRelevant());

		element.setRelevant(false);
		element.setValidated(true);
		JiraIssueCommentPersistenceManager.updateInDatabase(element);
		element = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		assertFalse(element.isRelevant());
		assertTrue(element.isValidated());
		assertTrue(element.getTypeAsString().equalsIgnoreCase("Other"));
	}
}
