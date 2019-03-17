package de.uhd.ifi.se.decision.management.jira.persistence.jiraissuecommentpersistencemanager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.comments.MutableComment;

import de.uhd.ifi.se.decision.management.jira.extraction.TestCommentSplitter;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfComment;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueCommentPersistenceManager;
import net.java.ao.test.jdbc.NonTransactional;

public class TestGetDecisionKnowledgeElementLong extends TestJiraIssueCommentPersistenceManagerSetUp {

	@Test
	@NonTransactional
	public void testElementExistingInAo() {
		List<PartOfComment> comment = TestCommentSplitter.getSentencesForCommentText("first Comment");
		long id = JiraIssueCommentPersistenceManager.insertDecisionKnowledgeElement(comment.get(0), null);
		assertNotNull(new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id));
	}

	@Test
	@NonTransactional
	public void testElementInsertedTwice() {
		List<PartOfComment> comment = TestCommentSplitter.getSentencesForCommentText("first Comment");
		long id = JiraIssueCommentPersistenceManager.insertDecisionKnowledgeElement(comment.get(0), null);
		long id2 = JiraIssueCommentPersistenceManager.insertDecisionKnowledgeElement(comment.get(0), null);
		assertNotNull(new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id));
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
		List<PartOfComment> comment = TestCommentSplitter.getSentencesForCommentText(
				"some sentence in front.  {pro} testobject {pro} some sentence in the back.");
		long id = JiraIssueCommentPersistenceManager.insertDecisionKnowledgeElement(comment.get(1), null);

		MutableComment comment2 = ComponentAccessor.getCommentManager()
				.getMutableComment(comment.get(1).getCommentId());
		ComponentAccessor.getCommentManager().delete(comment2);

		JiraIssueCommentPersistenceManager.cleanSentenceDatabaseForProject("TEST");

		PartOfComment element = (PartOfComment) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		assertNull(element);
	}

	@Test
	@NonTransactional
	public void testSetRelevantIntoAO() {
		List<PartOfComment> comment = TestCommentSplitter.getSentencesForCommentText("first Comment");
		long id = JiraIssueCommentPersistenceManager.insertDecisionKnowledgeElement(comment.get(0), null);

		PartOfComment element = (PartOfComment) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		assertFalse(element.isRelevant());

		element.setRelevant(true);
		JiraIssueCommentPersistenceManager.updateInDatabase(element);
		element = (PartOfComment) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		assertTrue(element.isRelevant());

		element.setRelevant(false);
		JiraIssueCommentPersistenceManager.updateInDatabase(element);
		element = (PartOfComment) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		assertFalse(element.isRelevant());
	}

	@Test
	@NonTransactional
	public void testSetRelevantIntoAOForNonExistingElement() {
		List<PartOfComment> comment = TestCommentSplitter.getSentencesForCommentText("first Comment");
		long id = JiraIssueCommentPersistenceManager.insertDecisionKnowledgeElement(comment.get(0), null);

		PartOfComment element = (PartOfComment) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		element.setRelevant(true);
		element.setId(id + 2);

		assertFalse(JiraIssueCommentPersistenceManager.updateInDatabase(element));
	}
}
