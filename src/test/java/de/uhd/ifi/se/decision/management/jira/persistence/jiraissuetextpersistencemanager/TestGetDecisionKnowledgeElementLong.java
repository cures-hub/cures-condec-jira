package de.uhd.ifi.se.decision.management.jira.persistence.jiraissuetextpersistencemanager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.comments.MutableComment;

import de.uhd.ifi.se.decision.management.jira.extraction.TestTextSplitter;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueTextPersistenceManager;
import net.java.ao.test.jdbc.NonTransactional;

public class TestGetDecisionKnowledgeElementLong extends TestJiraIssueCommentPersistenceManagerSetUp {

	@Test
	@NonTransactional
	public void testElementExistingInAo() {
		List<PartOfJiraIssueText> comment = TestTextSplitter.getSentencesForCommentText("first Comment");
		long id = JiraIssueTextPersistenceManager.insertDecisionKnowledgeElement(comment.get(0), null);
		assertNotNull(new JiraIssueTextPersistenceManager("").getDecisionKnowledgeElement(id));
	}

	@Test
	@NonTransactional
	public void testElementInsertedTwice() {
		List<PartOfJiraIssueText> comment = TestTextSplitter.getSentencesForCommentText("first Comment");
		long id = JiraIssueTextPersistenceManager.insertDecisionKnowledgeElement(comment.get(0), null);
		long id2 = JiraIssueTextPersistenceManager.insertDecisionKnowledgeElement(comment.get(0), null);
		assertNotNull(new JiraIssueTextPersistenceManager("").getDecisionKnowledgeElement(id));
		assertTrue(id == id2);
	}

	// @Test
	// @NonTransactional
	// public void testCommentHasChanged() {
	// TestComment tc = new TestComment();
	// List<Sentence> comment = tc.getSentencesForCommentText("first Comment");
	// long id =
	// JiraIssueCommentPersistenceManager.insertDecisionKnowledgeElement(comment.get(0),
	// null);
	//
	// List<Sentence> comment2 = tc.getSentencesForCommentText("second comment with
	// more text");
	// comment2.setJiraCommentId(comment.getJiraCommentId());
	//
	// Sentence element = (Sentence) new
	// JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
	//
	// // TODO @issue Is this the expected behaviour?
	// assertNotNull(element);
	// }

	@Test
	@NonTransactional
	public void testCleanSentenceDatabaseForProject() {
		List<PartOfJiraIssueText> partsOfText = TestTextSplitter.getSentencesForCommentText(
				"some sentence in front.  {pro} testobject {pro} some sentence in the back.");
		long id = JiraIssueTextPersistenceManager.insertDecisionKnowledgeElement(partsOfText.get(1), null);

		MutableComment comment = ComponentAccessor.getCommentManager()
				.getMutableComment(partsOfText.get(1).getCommentId());
		ComponentAccessor.getCommentManager().delete(comment);

		JiraIssueTextPersistenceManager.cleanSentenceDatabase("TEST");

		PartOfJiraIssueText element = (PartOfJiraIssueText) new JiraIssueTextPersistenceManager("").getDecisionKnowledgeElement(id);
		assertNull(element);
	}

	@Test
	@NonTransactional
	public void testSetRelevantIntoAO() {
		List<PartOfJiraIssueText> comment = TestTextSplitter.getSentencesForCommentText("first Comment");
		long id = JiraIssueTextPersistenceManager.insertDecisionKnowledgeElement(comment.get(0), null);

		PartOfJiraIssueText element = (PartOfJiraIssueText) new JiraIssueTextPersistenceManager("").getDecisionKnowledgeElement(id);
		assertFalse(element.isRelevant());

		element.setRelevant(true);
		JiraIssueTextPersistenceManager.updateInDatabase(element);
		element = (PartOfJiraIssueText) new JiraIssueTextPersistenceManager("").getDecisionKnowledgeElement(id);
		assertTrue(element.isRelevant());

		element.setRelevant(false);
		JiraIssueTextPersistenceManager.updateInDatabase(element);
		element = (PartOfJiraIssueText) new JiraIssueTextPersistenceManager("").getDecisionKnowledgeElement(id);
		assertFalse(element.isRelevant());
	}

	@Test
	@NonTransactional
	public void testSetRelevantIntoAOForNonExistingElement() {
		List<PartOfJiraIssueText> comment = TestTextSplitter.getSentencesForCommentText("first Comment");
		long id = JiraIssueTextPersistenceManager.insertDecisionKnowledgeElement(comment.get(0), null);

		PartOfJiraIssueText element = (PartOfJiraIssueText) new JiraIssueTextPersistenceManager("").getDecisionKnowledgeElement(id);
		element.setRelevant(true);
		element.setId(id + 2);

		assertFalse(JiraIssueTextPersistenceManager.updateInDatabase(element));
	}
}
