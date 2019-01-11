package de.uhd.ifi.se.decision.management.jira.persistence.jiraissuecommentpersistencemanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.atlassian.jira.component.ComponentAccessor;

import de.uhd.ifi.se.decision.management.jira.extraction.model.Comment;
import de.uhd.ifi.se.decision.management.jira.extraction.model.Sentence;
import de.uhd.ifi.se.decision.management.jira.extraction.model.impl.SentenceImpl;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueCommentPersistenceManager;
import net.java.ao.test.jdbc.NonTransactional;

public class TestUpdateDecisionKnowledgeElementSentenceUser extends TestJiraIssueCommentPersistenceMangerSetUp {

	@Test
	@NonTransactional
	public void testUpdateKnowledgeTypeSentenceNull() {
		assertFalse(new JiraIssueCommentPersistenceManager("").updateDecisionKnowledgeElement(null, null));
	}

	@Test
	@NonTransactional
	public void testUpdateKnowledgeTypeSentenceElementNull() {
		Sentence sentence = new SentenceImpl();
		sentence.setId(1000);
		assertFalse(new JiraIssueCommentPersistenceManager("").updateDecisionKnowledgeElement(sentence, null));
	}

	@Test
	@NonTransactional
	public void testUpdateKnowledgeType2() {
		Comment comment = getComment("first Comment");
		long id = TestJiraIssueCommentPersistenceMangerSetUp.insertDecisionKnowledgeElement(comment,
				comment.getIssueId(), 0);
		Sentence sentence = comment.getSentences().get(0);
		sentence.setType(KnowledgeType.ALTERNATIVE);
		new JiraIssueCommentPersistenceManager("").updateDecisionKnowledgeElement(sentence, null);
		Sentence element = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		assertTrue(element.getTypeAsString().equalsIgnoreCase("ALTERNATIVE"));
	}

	@Test
	@NonTransactional
	public void testUpdateKnowledgeType3() {
		Comment comment = getComment("first Comment");
		long id = TestJiraIssueCommentPersistenceMangerSetUp.insertDecisionKnowledgeElement(comment,
				comment.getIssueId(), 0);

		Sentence oldElement = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		oldElement.setType(KnowledgeType.ALTERNATIVE);
		new JiraIssueCommentPersistenceManager("").updateDecisionKnowledgeElement(oldElement, null);
		Sentence element = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		assertTrue(element.getTypeAsString().equalsIgnoreCase("ALTERNATIVE"));
	}

	@Test
	@NonTransactional
	public void testUpdateKnowledgeTypeWithManualTagged() {
		Comment comment = getComment("{issue} testobject {issue}");
		long id = TestJiraIssueCommentPersistenceMangerSetUp.insertDecisionKnowledgeElement(comment,
				comment.getIssueId(), 0);

		Sentence oldElement = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		oldElement.setType(KnowledgeType.ALTERNATIVE);
		new JiraIssueCommentPersistenceManager("").updateDecisionKnowledgeElement(oldElement, null);
		Sentence element = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		// Important that sentence object has no tags
		assertEquals("testobject", element.getBody().trim());
		assertEquals(element.getTypeAsString(), KnowledgeType.ALTERNATIVE.toString());
		assertEquals("{Alternative} testobject {Alternative}",
				ComponentAccessor.getCommentManager().getMutableComment((long) 0).getBody());
	}

	@Test
	@NonTransactional
	public void testUpdateKnowledgeTypeWithManualTaggedAndMoreSentences() {
		Comment comment = getComment("some sentence in front. {issue} testobject {issue}");
		long id = TestJiraIssueCommentPersistenceMangerSetUp.insertDecisionKnowledgeElement(comment,
				comment.getIssueId(), 1);

		Sentence oldElement = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		oldElement.setType(KnowledgeType.ALTERNATIVE);
		new JiraIssueCommentPersistenceManager("").updateDecisionKnowledgeElement(oldElement, null);
		Sentence element = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		assertEquals(element.getBody().trim(), "testobject");
		assertEquals(element.getTypeAsString(), KnowledgeType.ALTERNATIVE.toString());
		assertEquals("some sentence in front. {Alternative} testobject {Alternative}",
				ComponentAccessor.getCommentManager().getMutableComment((long) 0).getBody());
	}

	@Test
	@NonTransactional
	public void testUpdateKnowledgeTypeWithManualTaggedAndMoreSentences2() {
		Comment comment = getComment("some sentence in front. {issue} testobject {issue} some sentence in the back.");
		long id = TestJiraIssueCommentPersistenceMangerSetUp.insertDecisionKnowledgeElement(comment,
				comment.getIssueId(), 1);
		Sentence oldElement = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		oldElement.setType(KnowledgeType.ALTERNATIVE);
		new JiraIssueCommentPersistenceManager("").updateDecisionKnowledgeElement(oldElement, null);

		Sentence element = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		assertEquals(element.getBody(), " testobject ");
		assertEquals(element.getTypeAsString(), KnowledgeType.ALTERNATIVE.toString());
		assertEquals("some sentence in front. {Alternative} testobject {Alternative} some sentence in the back.",
				ComponentAccessor.getCommentManager().getMutableComment((long) 0).getBody());
	}

	@Test
	@NonTransactional
	public void testUpdateKnowledgeTypeWithManualTaggedAndMoreSentences2AndArgument() {
		Comment comment = getComment("some sentence in front. {issue} testobject {issue} some sentence in the back.");
		long id = TestJiraIssueCommentPersistenceMangerSetUp.insertDecisionKnowledgeElement(comment,
				comment.getIssueId(), 1);
		Sentence oldElement = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		assertEquals(oldElement.getType(), KnowledgeType.ISSUE);

		oldElement.setType(KnowledgeType.PRO);
		new JiraIssueCommentPersistenceManager("").updateDecisionKnowledgeElement(oldElement, null);
		Sentence element = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		assertEquals(" testobject ", element.getBody());
		assertEquals(element.getTypeAsString(), "Pro");

		assertEquals("some sentence in front. {Pro} testobject {Pro} some sentence in the back.",
				ComponentAccessor.getCommentManager().getMutableComment((long) 0).getBody());
	}

	@Test
	@NonTransactional
	public void testUpdateKnowledgeType3WithArgument() {
		Comment comment = getComment("first Comment");
		long id = TestJiraIssueCommentPersistenceMangerSetUp.insertDecisionKnowledgeElement(comment,
				comment.getIssueId(), 0);

		Sentence oldElement = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		oldElement.setType(KnowledgeType.PRO);
		new JiraIssueCommentPersistenceManager("").updateDecisionKnowledgeElement(oldElement, null);
		Sentence element = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		assertTrue(element.getTypeAsString().equalsIgnoreCase("Pro"));
	}

	@Test
	@NonTransactional
	public void testUpdateSentenceBodyWhenCommentChanged() {
		Comment comment = getComment("First sentences of two. Sencond sentences of two.");
		long id = TestJiraIssueCommentPersistenceMangerSetUp.insertDecisionKnowledgeElement(comment,
				comment.getIssueId(), 0);
		long id2 = TestJiraIssueCommentPersistenceMangerSetUp.insertDecisionKnowledgeElement(comment,
				comment.getIssueId(), 1);

		Sentence sentence = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);

		sentence.setDescription("secondComment with more text");
		new JiraIssueCommentPersistenceManager("").updateDecisionKnowledgeElement(sentence, null);

		Sentence element = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id2);
		assertTrue(element.getEndSubstringCount() != comment.getEndSubstringCount().get(1));
	}
}
