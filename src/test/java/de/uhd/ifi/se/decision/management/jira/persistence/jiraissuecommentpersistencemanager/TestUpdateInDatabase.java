package de.uhd.ifi.se.decision.management.jira.persistence.jiraissuecommentpersistencemanager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.model.JiraIssueComment;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Sentence;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueCommentPersistenceManager;
import net.java.ao.test.jdbc.NonTransactional;

public class TestUpdateInDatabase extends TestJiraIssueCommentPersistenceMangerSetUp {

	@Test
	@NonTransactional
	public void testUpdateSentenceElement() {
		JiraIssueComment comment = getComment("first Comment");
		long id = TestJiraIssueCommentPersistenceMangerSetUp.insertDecisionKnowledgeElement(comment,
				comment.getIssueId(), 0);
		Sentence sentence = comment.getSentences().get(0);
		sentence.setType("ALTERNATIVE");
		JiraIssueCommentPersistenceManager.updateInDatabase(sentence);
		Sentence element = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		assertTrue(element.getTypeAsString().equalsIgnoreCase("ALTERNATIVE"));
	}

	@Test
	@NonTransactional
	public void testUpdateKnowledgeType() {
		JiraIssueComment comment = getComment("first Comment");
		long id = TestJiraIssueCommentPersistenceMangerSetUp.insertDecisionKnowledgeElement(comment,
				comment.getIssueId(), 0);
		Sentence sentence = comment.getSentences().get(0);
		sentence.setType(KnowledgeType.ALTERNATIVE);
		JiraIssueCommentPersistenceManager.updateInDatabase(sentence);
		Sentence element = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		assertTrue(element.getTypeAsString().equalsIgnoreCase("ALTERNATIVE"));
	}

	@Test
	@NonTransactional
	public void testSetSentenceIrrlevant() {
		JiraIssueComment comment = getComment("first Comment");
		long id = TestJiraIssueCommentPersistenceMangerSetUp.insertDecisionKnowledgeElement(comment,
				comment.getIssueId(), 0);
		Sentence element = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		element.setRelevant(true);

		JiraIssueCommentPersistenceManager.updateInDatabase(element);
		element = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		assertTrue(element.isRelevant());

		element.setRelevant(false);
		element.setTagged(true);
		JiraIssueCommentPersistenceManager.updateInDatabase(element);
		element = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		assertFalse(element.isRelevant());
		assertTrue(element.isTagged());
		assertTrue(element.getTypeAsString().equalsIgnoreCase("Other"));
	}
}
