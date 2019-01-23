package de.uhd.ifi.se.decision.management.jira.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;

import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.extraction.TestCommentSplitter;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager;
import de.uhd.ifi.se.decision.management.jira.model.impl.SentenceImpl;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.NonTransactional;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TestSentence.AoSentenceTestDatabaseUpdater.class)
public class TestSentence extends TestSetUpWithIssues {

	private EntityManager entityManager;

	@Before
	public void setUp() {
		initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockUserManager());
	}

	@Test
	@NonTransactional
	public void testSetKnowledgeTypeEnum() {
		Sentence sentence = new SentenceImpl();
		assertNotNull(sentence);
		assertEquals(KnowledgeType.OTHER, sentence.getType());

		sentence.setType(KnowledgeType.ALTERNATIVE);
		assertEquals(KnowledgeType.ALTERNATIVE, sentence.getType());
	}

	@Test
	@NonTransactional
	public void testSetKnowledgeTypeString() {
		Sentence sentence = new SentenceImpl();
		sentence.setType(KnowledgeType.ALTERNATIVE.toString());
		assertEquals(KnowledgeType.ALTERNATIVE.toString(), sentence.getTypeAsString());
		sentence.setType("pro");
		assertEquals("Pro", sentence.getTypeAsString());
		sentence.setType("con");
		assertEquals(KnowledgeType.CON, sentence.getType());
	}

	@Test
	@NonTransactional
	public void testToString() {
		Sentence sentence = new SentenceImpl();
		sentence.setDescription("This is a decision.");
		assertEquals(sentence.toString(), "This is a decision.");
	}

	@Test
	@NonTransactional
	public void testGetKnowledgeTypeAsString() {
		Sentence sentence = new SentenceImpl();
		sentence.setType("");
		assertEquals("Other", sentence.getTypeAsString());
	}

	@Test
	@NonTransactional
	public void testGetCreated() {
		Sentence sentence = new SentenceImpl();
		sentence.setCreated(new Date());
		assertNotNull(sentence.getCreated());
	}

	@Test
	@NonTransactional
	public void testGetTextFromComment() {
		List<Sentence> sentences = TestCommentSplitter.getSentencesForCommentText(
				"some sentence in front. {issue} testobject {issue} some sentence in the back.");

		Sentence sentence = sentences.get(0);
		assertEquals(sentence.getTextFromComment(), "some sentence in front. ");
	}

	@Test
	@NonTransactional
	public void testGetTextFromCommentThatIsNull() {
		Sentence sentence = new SentenceImpl();
		assertEquals(sentence.getTextFromComment(), "");
		sentence.setDescription("This is a decision.");
		assertEquals(sentence.getTextFromComment(), "This is a decision.");
	}

	@Test
	@NonTransactional
	public void testIsTagged() {
		Sentence sentence = new SentenceImpl();
		assertFalse(sentence.isTagged());
		sentence.setType(KnowledgeType.CON);
		assertTrue(sentence.isTagged());
	}	

	@Test
	@NonTransactional
	public void testIsPlainText() {
		List<Sentence> sentences = TestCommentSplitter
				.getSentencesForCommentText("This is a text that is not classified.");
		assertEquals(true, sentences.get(0).isPlainText());
	}

	@Test
	@NonTransactional
	public void testIsPlainTextCode() {
		List<Sentence> sentences = TestCommentSplitter.getSentencesForCommentText("{code:Java} int i = 0 {code}");
		assertEquals(false, sentences.get(0).isPlainText());
	}

	@Test
	@NonTransactional
	public void testIsPlainTextAlternative() {
		List<Sentence> sentences = TestCommentSplitter
				.getSentencesForCommentText("{Alternative} This is an alternative. {Alternative}");
		assertEquals(true, sentences.get(0).isPlainText());
	}

	@Test
	@NonTransactional
	public void testIsPlainTextIcon() {
		List<Sentence> sentences = TestCommentSplitter.getSentencesForCommentText("(y) this is a icon pro text.");
		assertEquals(true, sentences.get(0).isPlainText());
	}
	
	@Test
	@NonTransactional
	public void testIsRelevantText() {
		List<Sentence> sentences = TestCommentSplitter.getSentencesForCommentText("This is a text that is not classified.");
		assertEquals(false, sentences.get(0).isRelevant());
	}

	@Test
	@NonTransactional
	public void testIsRelevantCode() {
		List<Sentence> sentences = TestCommentSplitter.getSentencesForCommentText("{code:Java} int i = 0 {code}");
		assertEquals(false, sentences.get(0).isRelevant());
	}

	@Test
	@NonTransactional
	public void testIsRelevantAlternative() {
		List<Sentence> sentences = TestCommentSplitter
				.getSentencesForCommentText("{Alternative} This is an alternative. {Alternative} ");
		assertEquals(true, sentences.get(0).isRelevant());
	}

	@Test
	@NonTransactional
	public void testIsRelevantIcon() {
		List<Sentence> sentences = TestCommentSplitter.getSentencesForCommentText("(y) this is a icon pro text.");
		assertEquals(true, sentences.get(0).isRelevant());
	}
	
	@Test
	@NonTransactional
	public void testIsValidatedText() {
		List<Sentence> sentences = TestCommentSplitter.getSentencesForCommentText("This is a text that is not classified.");
		assertEquals(false, sentences.get(0).isValidated());
	}

	@Test
	@NonTransactional
	public void testIsValidatedCode() {
		List<Sentence> sentences = TestCommentSplitter.getSentencesForCommentText("{code:Java} int i = 0 {code}");
		assertEquals(false, sentences.get(0).isValidated());
	}
	
	@Test
	@NonTransactional
	public void testIsValidatedAlternative() {
		List<Sentence> sentences = TestCommentSplitter
				.getSentencesForCommentText("{Alternative} This is an alternative. {Alternative} ");
		assertEquals(true, sentences.get(0).isValidated());
	}

	@Test
	@NonTransactional
	public void testIsValidatedIcon() {
		List<Sentence> sentences = TestCommentSplitter.getSentencesForCommentText("(y) this is a icon pro text.");
		assertEquals(true, sentences.get(0).isValidated());
	}
}
