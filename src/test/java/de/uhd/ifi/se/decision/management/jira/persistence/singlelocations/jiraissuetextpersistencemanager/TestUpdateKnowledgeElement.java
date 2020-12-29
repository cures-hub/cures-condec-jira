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
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssueTextPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestUpdateKnowledgeElement extends TestSetUp {

	protected static JiraIssueTextPersistenceManager manager;
	protected static ApplicationUser user;

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
		PartOfJiraIssueText element = JiraIssues.addElementToDataBase();
		assertTrue(element.getId() > 0);
		assertTrue(element.isRelevant());

		element.setRelevant(false);
		assertFalse(element.isRelevant());
		JiraIssueTextPersistenceManager.updateInDatabase(element);
		element = (PartOfJiraIssueText) manager.getKnowledgeElement(element);
		assertFalse(element.isRelevant());
	}

	@Test
	@NonTransactional
	public void testSetRelevantIntoAOForNonExistingElement() {
		List<PartOfJiraIssueText> comment = JiraIssues.getSentencesForCommentText("first Comment");

		PartOfJiraIssueText element = comment.get(0);
		element.setRelevant(true);
		element.setId(element.getId() + 2);

		assertFalse(JiraIssueTextPersistenceManager.updateInDatabase(element));
	}

	@Test
	@NonTransactional
	public void testUpdateKnowledgeTypeSentenceElementNull() {
		PartOfJiraIssueText sentence = new PartOfJiraIssueText();
		sentence.setId(1000);
		assertFalse(manager.updateKnowledgeElement(sentence, null));
	}

	@Test
	@NonTransactional
	public void testUpdateKnowledgeType2() {
		List<PartOfJiraIssueText> comment = JiraIssues.getSentencesForCommentText("first Comment");
		PartOfJiraIssueText sentence = (PartOfJiraIssueText) manager.insertKnowledgeElement(comment.get(0), null);
		sentence.setType(KnowledgeType.ALTERNATIVE);
		manager.updateKnowledgeElement(sentence, null);
		PartOfJiraIssueText element = (PartOfJiraIssueText) manager.getKnowledgeElement(sentence.getId());
		assertTrue(element.getTypeAsString().equalsIgnoreCase("ALTERNATIVE"));
	}

	@Test
	@NonTransactional
	public void testUpdateKnowledgeType3() {
		List<PartOfJiraIssueText> comment = JiraIssues.getSentencesForCommentText("first Comment");
		PartOfJiraIssueText oldElement = (PartOfJiraIssueText) manager.insertKnowledgeElement(comment.get(0), null);
		oldElement.setType(KnowledgeType.ALTERNATIVE);
		manager.updateKnowledgeElement(oldElement, null);
		PartOfJiraIssueText element = (PartOfJiraIssueText) manager.getKnowledgeElement(oldElement.getId());
		assertTrue(element.getTypeAsString().equalsIgnoreCase("ALTERNATIVE"));
	}

	@Test
	@NonTransactional
	public void testUpdateKnowledgeTypeWithManualTagged() {
		List<PartOfJiraIssueText> comment = JiraIssues.getSentencesForCommentText("{issue} testobject {issue}");
		long id = manager.insertKnowledgeElement(comment.get(0), null).getId();

		PartOfJiraIssueText oldElement = (PartOfJiraIssueText) manager.getKnowledgeElement(id);
		oldElement.setType(KnowledgeType.ALTERNATIVE);
		manager.updateKnowledgeElement(oldElement, null);
		PartOfJiraIssueText element = (PartOfJiraIssueText) manager.getKnowledgeElement(id);
		// Important that sentence object has no tags
		assertEquals("testobject", element.getDescription().trim());
		assertEquals(element.getTypeAsString(), KnowledgeType.ALTERNATIVE.toString());
		assertEquals("{alternative}testobject{alternative}",
				ComponentAccessor.getCommentManager().getMutableComment((long) 0).getBody());
	}

	@Test
	@NonTransactional
	public void testUpdateKnowledgeTypeWithManualTaggedAndMoreSentences() {
		List<PartOfJiraIssueText> comment = JiraIssues
				.getSentencesForCommentText("some sentence in front. {issue} testobject {issue}");
		long id = manager.insertKnowledgeElement(comment.get(1), null).getId();

		PartOfJiraIssueText oldElement = (PartOfJiraIssueText) manager.getKnowledgeElement(id);
		oldElement.setType(KnowledgeType.ALTERNATIVE);
		manager.updateKnowledgeElement(oldElement, null);
		PartOfJiraIssueText element = (PartOfJiraIssueText) manager.getKnowledgeElement(id);
		assertEquals(element.getDescription().trim(), "testobject");
		assertEquals(element.getTypeAsString(), KnowledgeType.ALTERNATIVE.toString());
		assertEquals("some sentence in front. {alternative}testobject{alternative}",
				ComponentAccessor.getCommentManager().getMutableComment((long) 0).getBody());
	}

	@Test
	@NonTransactional
	public void testUpdateKnowledgeTypeWithManualTaggedAndMoreSentences2() {
		List<PartOfJiraIssueText> comment = JiraIssues.getSentencesForCommentText(
				"some sentence in front. {issue} testobject {issue} some sentence in the back.");
		long id = manager.insertKnowledgeElement(comment.get(1), null).getId();
		PartOfJiraIssueText oldElement = (PartOfJiraIssueText) manager.getKnowledgeElement(id);
		oldElement.setType(KnowledgeType.ALTERNATIVE);
		manager.updateKnowledgeElement(oldElement, null);

		PartOfJiraIssueText element = (PartOfJiraIssueText) manager.getKnowledgeElement(id);
		assertEquals("testobject", element.getDescription());
		assertEquals(element.getTypeAsString(), KnowledgeType.ALTERNATIVE.toString());
		assertEquals("some sentence in front. {alternative}testobject{alternative} some sentence in the back.",
				ComponentAccessor.getCommentManager().getMutableComment((long) 0).getBody());
	}

	@Test
	@NonTransactional
	public void testUpdateKnowledgeTypeWithManualTaggedAndMoreSentences2AndArgument() {
		List<PartOfJiraIssueText> comment = JiraIssues.getSentencesForCommentText(
				"some sentence in front. {issue} testobject {issue} some sentence in the back.");
		long id = manager.insertKnowledgeElement(comment.get(1), null).getId();
		PartOfJiraIssueText oldElement = (PartOfJiraIssueText) manager.getKnowledgeElement(id);
		assertEquals(oldElement.getType(), KnowledgeType.ISSUE);

		oldElement.setType(KnowledgeType.PRO);
		manager.updateKnowledgeElement(oldElement, null);
		PartOfJiraIssueText element = (PartOfJiraIssueText) manager.getKnowledgeElement(id);
		assertEquals("testobject", element.getDescription());
		assertEquals(element.getTypeAsString(), "Pro");

		assertEquals("some sentence in front. {pro}testobject{pro} some sentence in the back.",
				ComponentAccessor.getCommentManager().getMutableComment((long) 0).getBody());
	}

	@Test
	@NonTransactional
	public void testUpdateKnowledgeType3WithArgument() {
		List<PartOfJiraIssueText> comment = JiraIssues.getSentencesForCommentText("first Comment");
		long id = manager.insertKnowledgeElement(comment.get(0), null).getId();

		PartOfJiraIssueText oldElement = (PartOfJiraIssueText) manager.getKnowledgeElement(id);
		oldElement.setType(KnowledgeType.PRO);
		manager.updateKnowledgeElement(oldElement, null);
		PartOfJiraIssueText element = (PartOfJiraIssueText) manager.getKnowledgeElement(id);
		assertTrue(element.getTypeAsString().equalsIgnoreCase("Pro"));
	}

	@Test
	@NonTransactional
	public void testUpdateSentenceBodyWhenCommentChanged() {
		List<PartOfJiraIssueText> comment = JiraIssues
				.getSentencesForCommentText("First sentences of two. Sencond sentences of two.");
		long id = manager.insertKnowledgeElement(comment.get(0), null).getId();
		long id2 = manager.insertKnowledgeElement(comment.get(1), null).getId();

		PartOfJiraIssueText sentence = (PartOfJiraIssueText) manager.getKnowledgeElement(id);

		sentence.setDescription("secondComment with more text");
		manager.updateKnowledgeElement(sentence, null);

		PartOfJiraIssueText element = (PartOfJiraIssueText) manager.getKnowledgeElement(id2);
		assertTrue(element.getEndPosition() != comment.get(1).getEndPosition());
	}
}
