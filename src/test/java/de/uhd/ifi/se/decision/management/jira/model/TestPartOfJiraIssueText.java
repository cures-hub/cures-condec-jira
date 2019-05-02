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
import com.atlassian.jira.issue.Issue;

import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.extraction.TestTextSplitter;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfText;
import de.uhd.ifi.se.decision.management.jira.model.text.impl.PartOfJiraIssueTextImpl;
import de.uhd.ifi.se.decision.management.jira.model.text.impl.PartOfTextImpl;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.NonTransactional;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TestPartOfJiraIssueText.AoSentenceTestDatabaseUpdater.class)
public class TestPartOfJiraIssueText extends TestSetUpWithIssues {

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
		PartOfJiraIssueText partOfText = new PartOfJiraIssueTextImpl();
		assertNotNull(partOfText);
		assertEquals(KnowledgeType.OTHER, partOfText.getType());

		partOfText.setType(KnowledgeType.ALTERNATIVE);
		assertEquals(KnowledgeType.ALTERNATIVE, partOfText.getType());
	}

	@Test
	@NonTransactional
	public void testSetKnowledgeTypeString() {
		PartOfJiraIssueText partOfText = new PartOfJiraIssueTextImpl();
		partOfText.setType(KnowledgeType.ALTERNATIVE.toString());
		assertEquals(KnowledgeType.ALTERNATIVE.toString(), partOfText.getTypeAsString());

		partOfText.setType("pro");
		assertEquals("Pro", partOfText.getTypeAsString());

		partOfText.setType("con");
		assertEquals(KnowledgeType.CON, partOfText.getType());
	}

	@Test
	@NonTransactional
	public void testToString() {
		PartOfJiraIssueText sentence = new PartOfJiraIssueTextImpl();
		sentence.setDescription("This is a decision.");
		assertEquals(sentence.toString(), "This is a decision.");
	}

	@Test
	@NonTransactional
	public void testGetKnowledgeTypeAsString() {
		PartOfJiraIssueText sentence = new PartOfJiraIssueTextImpl();
		sentence.setType("");
		assertEquals("Other", sentence.getTypeAsString());
	}

	@Test
	@NonTransactional
	public void testGetCreated() {
		PartOfJiraIssueText sentence = new PartOfJiraIssueTextImpl();
		sentence.setCreated(new Date());
		assertNotNull(sentence.getCreated());
	}

	@Test
	@NonTransactional
	public void testGetTextFromComment() {
		List<PartOfJiraIssueText> partsOfText = TestTextSplitter.getSentencesForCommentText(
				"some sentence in front. {issue} testobject {issue} some sentence in the back.");

		PartOfJiraIssueText partOfText = partsOfText.get(0);
		assertEquals(partOfText.getText(), "some sentence in front. ");
	}

	@Test
	@NonTransactional
	public void testGetTextFromCommentThatIsNull() {
		PartOfJiraIssueText sentence = new PartOfJiraIssueTextImpl();
		assertEquals(sentence.getText(), "");
	}
	
	@Test
	@NonTransactional
	public void testGetTextInSuperClass() {
		PartOfText partOfText = new PartOfTextImpl();
		assertEquals(partOfText.getText(), "");
	}

	@Test
	@NonTransactional
	public void testIsTagged() {
		PartOfJiraIssueText partOfText = new PartOfJiraIssueTextImpl();
		assertFalse(partOfText.isTagged());

		partOfText.setType(KnowledgeType.CON);
		assertTrue(partOfText.isTagged());
	}

	@Test
	@NonTransactional
	public void testIsPlainText() {
		List<PartOfJiraIssueText> partsOfText = TestTextSplitter
				.getSentencesForCommentText("This is a text that is not classified.");
		assertTrue(partsOfText.get(0).isPlainText());
	}

	@Test
	@NonTransactional
	public void testIsPlainTextCode() {
		List<PartOfJiraIssueText> partsOfText = TestTextSplitter
				.getSentencesForCommentText("{code:Java} int i = 0 {code}");
		assertFalse(partsOfText.get(0).isPlainText());
	}

	@Test
	@NonTransactional
	public void testIsPlainTextAlternative() {
		List<PartOfJiraIssueText> partsOfText = TestTextSplitter
				.getSentencesForCommentText("{Alternative} This is an alternative. {Alternative}");
		assertTrue(partsOfText.get(0).isPlainText());
	}

	@Test
	@NonTransactional
	public void testIsPlainTextIcon() {
		List<PartOfJiraIssueText> partsOfText = TestTextSplitter
				.getSentencesForCommentText("(y) this is a icon pro text.");
		assertTrue(partsOfText.get(0).isPlainText());
	}

	@Test
	@NonTransactional
	public void testIsRelevantText() {
		List<PartOfJiraIssueText> partsOfText = TestTextSplitter
				.getSentencesForCommentText("This is a text that is not classified.");
		assertFalse(partsOfText.get(0).isRelevant());
	}

	@Test
	@NonTransactional
	public void testIsRelevantCode() {
		List<PartOfJiraIssueText> partsOfText = TestTextSplitter
				.getSentencesForCommentText("{code:Java} int i = 0 {code}");
		assertFalse(partsOfText.get(0).isRelevant());
	}

	@Test
	@NonTransactional
	public void testIsRelevantAlternative() {
		List<PartOfJiraIssueText> partsOfText = TestTextSplitter
				.getSentencesForCommentText("{Alternative} This is an alternative. {Alternative} ");
		assertTrue(partsOfText.get(0).isRelevant());
	}

	@Test
	@NonTransactional
	public void testIsRelevantIcon() {
		List<PartOfJiraIssueText> partsOfText = TestTextSplitter
				.getSentencesForCommentText("(y) this is a icon pro text.");
		assertTrue(partsOfText.get(0).isRelevant());
	}

	@Test
	@NonTransactional
	public void testIsValidatedText() {
		List<PartOfJiraIssueText> partsOfText = TestTextSplitter
				.getSentencesForCommentText("This is a text that is not classified.");
		assertFalse(partsOfText.get(0).isValidated());
	}

	@Test
	@NonTransactional
	public void testIsValidatedCode() {
		List<PartOfJiraIssueText> partsOfText = TestTextSplitter
				.getSentencesForCommentText("{code:Java} int i = 0 {code}");
		assertFalse(partsOfText.get(0).isValidated());
	}

	@Test
	@NonTransactional
	public void testIsValidatedAlternative() {
		List<PartOfJiraIssueText> partsOfText = TestTextSplitter
				.getSentencesForCommentText("{alternative} This is an alternative. {alternative} ");
		assertTrue(partsOfText.get(0).isValidated());
	}

	@Test
	@NonTransactional
	public void testIsValidatedIssue() {
		List<PartOfJiraIssueText> partsOfText = TestTextSplitter
				.getSentencesForCommentText("{issue} This is an alternative. {issue} ");
		assertTrue(partsOfText.get(0).isValidated());
	}

	@Test
	@NonTransactional
	public void testIsValidatedIcon() {
		List<PartOfJiraIssueText> partsOfText = TestTextSplitter
				.getSentencesForCommentText("(y) this is a icon pro text.");
		assertTrue(partsOfText.get(0).isValidated());
	}

	@Test
	@NonTransactional
	public void testGetLength() {
		List<PartOfJiraIssueText> partsOfText = TestTextSplitter
				.getSentencesForCommentText("(y) this is a icon pro text.");
		assertEquals(28, partsOfText.get(0).getLength());
	}

	@Test
	@NonTransactional
	public void testGetJiraIssue() {
		PartOfJiraIssueText partOfText = new PartOfJiraIssueTextImpl();
		partOfText.setJiraIssueId(50);
		Issue jiraIssue = partOfText.getJiraIssue();
		assertNotNull(jiraIssue);
		assertEquals(50, (long) jiraIssue.getId());
	}
	
	@Test
	@NonTransactional
	public void testGetJiraIssueDescription() {
		PartOfJiraIssueText partOfText = new PartOfJiraIssueTextImpl();		
		assertEquals("", partOfText.getJiraIssueDescription());
		
		partOfText.setJiraIssueId(50);

		assertEquals("Test", partOfText.getJiraIssueDescription());
	}
}
