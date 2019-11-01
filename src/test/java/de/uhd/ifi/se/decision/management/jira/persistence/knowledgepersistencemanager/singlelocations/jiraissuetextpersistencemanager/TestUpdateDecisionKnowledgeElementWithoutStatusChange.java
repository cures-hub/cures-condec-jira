package de.uhd.ifi.se.decision.management.jira.persistence.knowledgepersistencemanager.singlelocations.jiraissuetextpersistencemanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.extraction.TestTextSplitter;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.model.text.impl.PartOfJiraIssueTextImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.JiraIssueTextPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestUpdateDecisionKnowledgeElementWithoutStatusChange extends TestSetUp {

	protected static JiraIssueTextPersistenceManager manager;
	protected static ApplicationUser user;

	@Before
	public void setUp() {
		init();
		manager = new JiraIssueTextPersistenceManager("TEST");
		user = JiraUsers.SYS_ADMIN.getApplicationUser();
	}

	@Test
	@NonTransactional
	public void testUpdateKnowledgeTypeSentenceNull() {
		assertFalse(
				new JiraIssueTextPersistenceManager("").updateDecisionKnowledgeElementWithoutStatusChange(null, null));
	}

	@Test
	@NonTransactional
	public void testUpdateKnowledgeTypeSentenceElementNull() {
		PartOfJiraIssueText sentence = new PartOfJiraIssueTextImpl();
		sentence.setId(1000);
		assertFalse(new JiraIssueTextPersistenceManager("").updateDecisionKnowledgeElementWithoutStatusChange(sentence,
				null));
	}

	@Test
	@NonTransactional
	public void testUpdateKnowledgeType2() {
		List<PartOfJiraIssueText> comment = TestTextSplitter.getSentencesForCommentText("first Comment");
		long id = manager.insertDecisionKnowledgeElement(comment.get(0), null);
		PartOfJiraIssueText sentence = comment.get(0);
		sentence.setType(KnowledgeType.ALTERNATIVE);
		new JiraIssueTextPersistenceManager("").updateDecisionKnowledgeElementWithoutStatusChange(sentence, null);
		PartOfJiraIssueText element = (PartOfJiraIssueText) new JiraIssueTextPersistenceManager("")
				.getDecisionKnowledgeElement(id);
		assertTrue(element.getTypeAsString().equalsIgnoreCase("ALTERNATIVE"));
	}

	@Test
	@NonTransactional
	public void testUpdateKnowledgeType3() {
		List<PartOfJiraIssueText> comment = TestTextSplitter.getSentencesForCommentText("first Comment");
		long id = manager.insertDecisionKnowledgeElement(comment.get(0), null);

		PartOfJiraIssueText oldElement = (PartOfJiraIssueText) new JiraIssueTextPersistenceManager("")
				.getDecisionKnowledgeElement(id);
		oldElement.setType(KnowledgeType.ALTERNATIVE);
		new JiraIssueTextPersistenceManager("").updateDecisionKnowledgeElementWithoutStatusChange(oldElement, null);
		PartOfJiraIssueText element = (PartOfJiraIssueText) new JiraIssueTextPersistenceManager("")
				.getDecisionKnowledgeElement(id);
		assertTrue(element.getTypeAsString().equalsIgnoreCase("ALTERNATIVE"));
	}

	@Test
	@NonTransactional
	public void testUpdateKnowledgeTypeWithManualTagged() {
		List<PartOfJiraIssueText> comment = TestTextSplitter.getSentencesForCommentText("{issue} testobject {issue}");
		long id = manager.insertDecisionKnowledgeElement(comment.get(0), null);

		PartOfJiraIssueText oldElement = (PartOfJiraIssueText) new JiraIssueTextPersistenceManager("")
				.getDecisionKnowledgeElement(id);
		oldElement.setType(KnowledgeType.ALTERNATIVE);
		new JiraIssueTextPersistenceManager("").updateDecisionKnowledgeElementWithoutStatusChange(oldElement, null);
		PartOfJiraIssueText element = (PartOfJiraIssueText) new JiraIssueTextPersistenceManager("")
				.getDecisionKnowledgeElement(id);
		// Important that sentence object has no tags
		assertEquals("testobject", element.getDescription().trim());
		assertEquals(element.getTypeAsString(), KnowledgeType.ALTERNATIVE.toString());
		assertEquals("{alternative} testobject {alternative}",
				ComponentAccessor.getCommentManager().getMutableComment((long) 0).getBody());
	}

	@Test
	@NonTransactional
	public void testUpdateKnowledgeTypeWithManualTaggedAndMoreSentences() {
		List<PartOfJiraIssueText> comment = TestTextSplitter
				.getSentencesForCommentText("some sentence in front. {issue} testobject {issue}");
		long id = manager.insertDecisionKnowledgeElement(comment.get(1), null);

		PartOfJiraIssueText oldElement = (PartOfJiraIssueText) new JiraIssueTextPersistenceManager("")
				.getDecisionKnowledgeElement(id);
		oldElement.setType(KnowledgeType.ALTERNATIVE);
		new JiraIssueTextPersistenceManager("").updateDecisionKnowledgeElementWithoutStatusChange(oldElement, null);
		PartOfJiraIssueText element = (PartOfJiraIssueText) new JiraIssueTextPersistenceManager("")
				.getDecisionKnowledgeElement(id);
		assertEquals(element.getDescription().trim(), "testobject");
		assertEquals(element.getTypeAsString(), KnowledgeType.ALTERNATIVE.toString());
		assertEquals("some sentence in front. {alternative} testobject {alternative}",
				ComponentAccessor.getCommentManager().getMutableComment((long) 0).getBody());
	}

	@Test
	@NonTransactional
	public void testUpdateKnowledgeTypeWithManualTaggedAndMoreSentences2() {
		List<PartOfJiraIssueText> comment = TestTextSplitter.getSentencesForCommentText(
				"some sentence in front. {issue} testobject {issue} some sentence in the back.");
		long id = manager.insertDecisionKnowledgeElement(comment.get(1), null);
		PartOfJiraIssueText oldElement = (PartOfJiraIssueText) new JiraIssueTextPersistenceManager("")
				.getDecisionKnowledgeElement(id);
		oldElement.setType(KnowledgeType.ALTERNATIVE);
		new JiraIssueTextPersistenceManager("").updateDecisionKnowledgeElementWithoutStatusChange(oldElement, null);

		PartOfJiraIssueText element = (PartOfJiraIssueText) new JiraIssueTextPersistenceManager("")
				.getDecisionKnowledgeElement(id);
		assertEquals(element.getDescription(), " testobject ");
		assertEquals(element.getTypeAsString(), KnowledgeType.ALTERNATIVE.toString());
		assertEquals("some sentence in front. {alternative} testobject {alternative} some sentence in the back.",
				ComponentAccessor.getCommentManager().getMutableComment((long) 0).getBody());
	}

	@Test
	@NonTransactional
	public void testUpdateKnowledgeTypeWithManualTaggedAndMoreSentences2AndArgument() {
		List<PartOfJiraIssueText> comment = TestTextSplitter.getSentencesForCommentText(
				"some sentence in front. {issue} testobject {issue} some sentence in the back.");
		long id = manager.insertDecisionKnowledgeElement(comment.get(1), null);
		PartOfJiraIssueText oldElement = (PartOfJiraIssueText) new JiraIssueTextPersistenceManager("")
				.getDecisionKnowledgeElement(id);
		assertEquals(oldElement.getType(), KnowledgeType.ISSUE);

		oldElement.setType(KnowledgeType.PRO);
		new JiraIssueTextPersistenceManager("").updateDecisionKnowledgeElementWithoutStatusChange(oldElement, null);
		PartOfJiraIssueText element = (PartOfJiraIssueText) new JiraIssueTextPersistenceManager("")
				.getDecisionKnowledgeElement(id);
		assertEquals(" testobject ", element.getDescription());
		assertEquals(element.getTypeAsString(), "Pro");

		assertEquals("some sentence in front. {pro} testobject {pro} some sentence in the back.",
				ComponentAccessor.getCommentManager().getMutableComment((long) 0).getBody());
	}

	@Test
	@NonTransactional
	public void testUpdateKnowledgeType3WithArgument() {
		List<PartOfJiraIssueText> comment = TestTextSplitter.getSentencesForCommentText("first Comment");
		long id = manager.insertDecisionKnowledgeElement(comment.get(0), null);

		PartOfJiraIssueText oldElement = (PartOfJiraIssueText) new JiraIssueTextPersistenceManager("")
				.getDecisionKnowledgeElement(id);
		oldElement.setType(KnowledgeType.PRO);
		new JiraIssueTextPersistenceManager("").updateDecisionKnowledgeElementWithoutStatusChange(oldElement, null);
		PartOfJiraIssueText element = (PartOfJiraIssueText) new JiraIssueTextPersistenceManager("")
				.getDecisionKnowledgeElement(id);
		assertTrue(element.getTypeAsString().equalsIgnoreCase("Pro"));
	}

	@Test
	@NonTransactional
	public void testUpdateSentenceBodyWhenCommentChanged() {
		List<PartOfJiraIssueText> comment = TestTextSplitter
				.getSentencesForCommentText("First sentences of two. Sencond sentences of two.");
		long id = manager.insertDecisionKnowledgeElement(comment.get(0), null);
		long id2 = manager.insertDecisionKnowledgeElement(comment.get(1), null);

		PartOfJiraIssueText sentence = (PartOfJiraIssueText) new JiraIssueTextPersistenceManager("")
				.getDecisionKnowledgeElement(id);

		sentence.setDescription("secondComment with more text");
		new JiraIssueTextPersistenceManager("").updateDecisionKnowledgeElementWithoutStatusChange(sentence, null);

		PartOfJiraIssueText element = (PartOfJiraIssueText) new JiraIssueTextPersistenceManager("")
				.getDecisionKnowledgeElement(id2);
		assertTrue(element.getEndPosition() != comment.get(1).getEndPosition());
	}
}
