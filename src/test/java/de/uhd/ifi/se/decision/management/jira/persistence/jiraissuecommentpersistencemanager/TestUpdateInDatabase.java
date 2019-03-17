package de.uhd.ifi.se.decision.management.jira.persistence.jiraissuecommentpersistencemanager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.extraction.TestCommentSplitter;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfComment;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueCommentPersistenceManager;
import net.java.ao.test.jdbc.NonTransactional;

public class TestUpdateInDatabase extends TestJiraIssueCommentPersistenceManagerSetUp {

	@Test
	@NonTransactional
	public void testUpdateSentenceElement() {
		List<PartOfComment> comment = TestCommentSplitter.getSentencesForCommentText("first Comment");
		long id = JiraIssueCommentPersistenceManager.insertDecisionKnowledgeElement(comment.get(0), null);
		PartOfComment sentence = comment.get(0);
		sentence.setType("ALTERNATIVE");
		JiraIssueCommentPersistenceManager.updateInDatabase(sentence);
		PartOfComment element = (PartOfComment) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		assertTrue(element.getTypeAsString().equalsIgnoreCase("ALTERNATIVE"));
	}

	@Test
	@NonTransactional
	public void testUpdateKnowledgeType() {
		List<PartOfComment> comment = TestCommentSplitter.getSentencesForCommentText("first Comment");
		long id = JiraIssueCommentPersistenceManager.insertDecisionKnowledgeElement(comment.get(0), null);
		PartOfComment sentence = comment.get(0);
		sentence.setType(KnowledgeType.ALTERNATIVE);
		JiraIssueCommentPersistenceManager.updateInDatabase(sentence);
		PartOfComment element = (PartOfComment) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		assertTrue(element.getTypeAsString().equalsIgnoreCase("ALTERNATIVE"));
	}

	@Test
	@NonTransactional
	public void testSetSentenceIrrlevant() {
		List<PartOfComment> comment = TestCommentSplitter.getSentencesForCommentText("first Comment");
		long id = JiraIssueCommentPersistenceManager.insertDecisionKnowledgeElement(comment.get(0), null);
		PartOfComment element = (PartOfComment) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		element.setRelevant(true);

		JiraIssueCommentPersistenceManager.updateInDatabase(element);
		element = (PartOfComment) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		assertTrue(element.isRelevant());

		element.setRelevant(false);
		element.setValidated(true);
		JiraIssueCommentPersistenceManager.updateInDatabase(element);
		element = (PartOfComment) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		assertFalse(element.isRelevant());
		assertTrue(element.isValidated());
		assertTrue(element.getTypeAsString().equalsIgnoreCase("Other"));
	}
}
