package de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.jiraissuetextpersistencemanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssueTextPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;

public class TestUpdateKnowledgeElement extends TestSetUp {

	private JiraIssueTextPersistenceManager manager;
	private ApplicationUser user;

	@Before
	public void setUp() {
		init();
		manager = KnowledgePersistenceManager.getOrCreate("TEST").getJiraIssueTextManager();
		user = JiraUsers.SYS_ADMIN.getApplicationUser();
	}

	@Test
	@NonTransactional
	public void testElementNullUserNull() {
		assertFalse(manager.updateKnowledgeElement(null, null));
	}

	@Test
	@NonTransactional
	public void testSetRelevant() {
		PartOfJiraIssueText sentence = JiraIssues.getIrrelevantSentence();
		assertFalse(sentence.isRelevant());
		sentence.setRelevant(true);
		assertTrue(sentence.isRelevant());

		manager.updateKnowledgeElement(sentence, user);
		sentence = (PartOfJiraIssueText) manager.getKnowledgeElement(sentence);
		assertTrue(sentence.isRelevant());
	}

	@Test
	@NonTransactional
	public void testNonExistingElement() {
		PartOfJiraIssueText sentence = JiraIssues.getIrrelevantSentence();
		sentence.setRelevant(true);
		sentence.setId(sentence.getId() + 2);
		assertFalse(manager.updateKnowledgeElement(sentence, user));
	}

	@Test
	@NonTransactional
	public void testChangeKnowledgeTypeOfIrrelevantSentence() {
		PartOfJiraIssueText sentence = JiraIssues.getIrrelevantSentence();
		sentence.setType(KnowledgeType.ALTERNATIVE);
		manager.updateKnowledgeElement(sentence, user);
		PartOfJiraIssueText element = (PartOfJiraIssueText) manager.getKnowledgeElement(sentence);
		assertEquals(KnowledgeType.ALTERNATIVE, element.getType());
		assertEquals("{alternative}This is a test sentence.{alternative}",
				ComponentAccessor.getCommentManager().getMutableComment((long) 0).getBody());
	}

	@Test
	@NonTransactional
	public void testChangeKnowledgeTypeWithManualTagged() {
		List<PartOfJiraIssueText> comment = JiraIssues
				.getSentencesForCommentText("{issue} This is a test sentence. {issue}");
		PartOfJiraIssueText oldElement = comment.get(0);

		oldElement.setType(KnowledgeType.ALTERNATIVE);
		manager.updateKnowledgeElement(oldElement, user);

		PartOfJiraIssueText element = (PartOfJiraIssueText) manager.getKnowledgeElement(oldElement);
		// Important that sentence object has no tags
		assertEquals("This is a test sentence.", element.getDescription());
		assertEquals(KnowledgeType.ALTERNATIVE, element.getType());
		assertEquals("{alternative}This is a test sentence.{alternative}",
				ComponentAccessor.getCommentManager().getMutableComment((long) 0).getBody());
	}

	@Test
	@NonTransactional
	public void testChangeKnowledgeTypeWithManualTaggedAndMoreSentences() {
		List<PartOfJiraIssueText> comment = JiraIssues.getSentencesForCommentText(
				"A sentence in front. {issue} This is a test sentence. {issue} some sentence in the back.");
		PartOfJiraIssueText oldElement = comment.get(1);
		assertEquals(KnowledgeType.ISSUE, oldElement.getType());

		oldElement.setType(KnowledgeType.PRO);
		manager.updateKnowledgeElement(oldElement, user);
		PartOfJiraIssueText element = (PartOfJiraIssueText) manager.getKnowledgeElement(oldElement);
		assertEquals("This is a test sentence.", element.getDescription());
		assertEquals(KnowledgeType.PRO, element.getType());

		assertEquals("A sentence in front. {pro}This is a test sentence.{pro} some sentence in the back.",
				ComponentAccessor.getCommentManager().getMutableComment((long) 0).getBody());
	}

	@Test
	@NonTransactional
	public void testUpdateSentencePositionWhenCommentChanged() {
		List<PartOfJiraIssueText> comment = JiraIssues
				.getSentencesForCommentText("First sentences of two. Sencond sentence of two.");
		PartOfJiraIssueText firstSentence = comment.get(0);
		firstSentence.setDescription("First sentences of two with more text. ");
		manager.updateKnowledgeElement(firstSentence, user);

		assertEquals("First sentences of two with more text. Sencond sentence of two.",
				ComponentAccessor.getCommentManager().getMutableComment((long) 0).getBody());

		PartOfJiraIssueText secondSentenceAfterUpdate = (PartOfJiraIssueText) manager
				.getKnowledgeElement(comment.get(1));
		assertTrue(secondSentenceAfterUpdate.getEndPosition() != comment.get(1).getEndPosition());
	}

	@Test
	@NonTransactional
	public void testUpdateElementWithWrongDocumentationLocation() {
		KnowledgeElement element = KnowledgeElements.getTestKnowledgeElement();
		assertEquals(DocumentationLocation.JIRAISSUE, element.getDocumentationLocation());
		assertFalse(manager.updateKnowledgeElement(element, user));
	}
}
