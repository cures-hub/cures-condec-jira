package de.uhd.ifi.se.decision.management.jira.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.issue.Issue;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestPartOfJiraIssueText extends TestSetUp {

	@Before
	public void setUp() {
		init();
	}

	@Test
	@NonTransactional
	public void testSetKnowledgeTypeEnum() {
		PartOfJiraIssueText partOfText = new PartOfJiraIssueText();
		assertNotNull(partOfText);
		assertEquals(KnowledgeType.OTHER, partOfText.getType());

		partOfText.setType(KnowledgeType.ALTERNATIVE);
		assertEquals(KnowledgeType.ALTERNATIVE, partOfText.getType());
	}

	@Test
	@NonTransactional
	public void testSetKnowledgeTypeString() {
		PartOfJiraIssueText partOfText = new PartOfJiraIssueText();
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
		PartOfJiraIssueText sentence = new PartOfJiraIssueText();
		sentence.setDescription("This is a decision.");
		assertEquals(sentence.toString(), "This is a decision.");
	}

	@Test
	@NonTransactional
	public void testGetKnowledgeTypeAsString() {
		PartOfJiraIssueText sentence = new PartOfJiraIssueText();
		sentence.setType("");
		assertEquals("Other", sentence.getTypeAsString());
	}

	@Test
	@NonTransactional
	public void testGetCreated() {
		PartOfJiraIssueText sentence = new PartOfJiraIssueText();
		sentence.setCreationDate(new Date());
		assertNotNull(sentence.getCreationDate());
	}

	@Test
	@NonTransactional
	public void testGetTextFromComment() {
		List<PartOfJiraIssueText> partsOfText = JiraIssues.getSentencesForCommentText(
				"some sentence in front. {issue} testobject {issue} some sentence in the back.");

		PartOfJiraIssueText partOfText = partsOfText.get(0);
		assertEquals(partOfText.getText(), "some sentence in front. ");
	}

	@Test
	@NonTransactional
	public void testGetTextFromCommentThatIsNull() {
		PartOfJiraIssueText sentence = new PartOfJiraIssueText();
		assertEquals(sentence.getText(), "");
	}

	@Test
	@NonTransactional
	public void testIsTagged() {
		PartOfJiraIssueText partOfText = new PartOfJiraIssueText();
		assertFalse(partOfText.isTagged());

		partOfText.setType(KnowledgeType.CON);
		assertTrue(partOfText.isTagged());
	}

	@Test
	@NonTransactional
	public void testIsPlainText() {
		List<PartOfJiraIssueText> partsOfText = JiraIssues
				.getSentencesForCommentText("This is a text that is not classified.");
		assertTrue(partsOfText.get(0).isPlainText());
	}

	@Test
	@NonTransactional
	public void testIsPlainTextCode() {
		List<PartOfJiraIssueText> partsOfText = JiraIssues.getSentencesForCommentText("{code:Java} int i = 0 {code}");
		assertFalse(partsOfText.get(0).isPlainText());
	}

	@Test
	@NonTransactional
	public void testIsPlainTextAlternative() {
		List<PartOfJiraIssueText> partsOfText = JiraIssues
				.getSentencesForCommentText("{Alternative} This is an alternative. {Alternative}");
		assertTrue(partsOfText.get(0).isPlainText());
	}

	@Test
	@NonTransactional
	public void testIsPlainTextIcon() {
		List<PartOfJiraIssueText> partsOfText = JiraIssues.getSentencesForCommentText("(y) this is a icon pro text.");
		assertTrue(partsOfText.get(0).isPlainText());
	}

	@Test
	@NonTransactional
	public void testIsRelevantText() {
		List<PartOfJiraIssueText> partsOfText = JiraIssues
				.getSentencesForCommentText("This is a text that is not classified.");
		assertFalse(partsOfText.get(0).isRelevant());
	}

	@Test
	@NonTransactional
	public void testIsRelevantCode() {
		List<PartOfJiraIssueText> partsOfText = JiraIssues.getSentencesForCommentText("{code:Java} int i = 0 {code}");
		assertFalse(partsOfText.get(0).isRelevant());
	}

	@Test
	@NonTransactional
	public void testIsRelevantAlternative() {
		List<PartOfJiraIssueText> partsOfText = JiraIssues
				.getSentencesForCommentText("{Alternative} This is an alternative. {Alternative} ");
		assertTrue(partsOfText.get(0).isRelevant());
	}

	@Test
	@NonTransactional
	public void testIsRelevantIcon() {
		List<PartOfJiraIssueText> partsOfText = JiraIssues.getSentencesForCommentText("(y) this is a icon pro text.");
		assertTrue(partsOfText.get(0).isRelevant());
	}

	@Test
	@NonTransactional
	public void testIsValidatedText() {
		List<PartOfJiraIssueText> partsOfText = JiraIssues
				.getSentencesForCommentText("This is a text that is not classified.");
		assertFalse(partsOfText.get(0).isValidated());
	}

	@Test
	@NonTransactional
	public void testIsValidatedCode() {
		List<PartOfJiraIssueText> partsOfText = JiraIssues.getSentencesForCommentText("{code:Java} int i = 0 {code}");
		assertFalse(partsOfText.get(0).isValidated());
	}

	@Test
	@NonTransactional
	public void testIsValidatedAlternative() {
		List<PartOfJiraIssueText> partsOfText = JiraIssues
				.getSentencesForCommentText("{alternative} This is an alternative. {alternative} ");
		assertTrue(partsOfText.get(0).isValidated());
	}

	@Test
	@NonTransactional
	public void testIsValidatedIssue() {
		List<PartOfJiraIssueText> partsOfText = JiraIssues
				.getSentencesForCommentText("{issue} This is an alternative. {issue} ");
		assertTrue(partsOfText.get(0).isValidated());
	}

	@Test
	@NonTransactional
	public void testIsValidatedIcon() {
		List<PartOfJiraIssueText> partsOfText = JiraIssues.getSentencesForCommentText("(y) this is a icon pro text.");
		assertTrue(partsOfText.get(0).isValidated());
	}

	@Test
	@NonTransactional
	public void testGetLength() {
		List<PartOfJiraIssueText> partsOfText = JiraIssues.getSentencesForCommentText("(y) this is a icon pro text.");
		assertEquals(28, partsOfText.get(0).getLength());
	}

	@Test
	@NonTransactional
	public void testGetJiraIssue() {
		PartOfJiraIssueText partOfText = new PartOfJiraIssueText();
		partOfText.setJiraIssueId(1);
		Issue jiraIssue = partOfText.getJiraIssue();
		assertNotNull(jiraIssue);
		assertEquals(1, (long) jiraIssue.getId());
	}

	@Test
	@NonTransactional
	public void testGetCreatorOfComment() {
		List<PartOfJiraIssueText> partsOfText = JiraIssues
				.getSentencesForCommentText("{Alternative} This is an alternative. {Alternative} ");
		assertEquals(JiraUsers.SYS_ADMIN.getApplicationUser(), partsOfText.get(0).getCreator());
	}

	@Test
	@NonTransactional
	public void testGetCreatorOfDescription() {
		List<PartOfJiraIssueText> partsOfText = JiraIssues
				.getSentencesForCommentText("{Alternative} This is an alternative. {Alternative} ");
		// this means that the element is documented in the description
		partsOfText.get(0).setCommentId(0);
		assertEquals(JiraUsers.SYS_ADMIN.getApplicationUser(), partsOfText.get(0).getCreator());
	}

	@Test
	@NonTransactional
	public void testIsValid() {
		List<PartOfJiraIssueText> partsOfText = JiraIssues
				.getSentencesForCommentText("{Alternative} This is an alternative. {Alternative} ");
		PartOfJiraIssueText partOfText = partsOfText.get(0);

		assertTrue(partOfText.isValid());

		// this means that the element is documented in the description
		partOfText.setCommentId(0);
		assertTrue(partOfText.isValid());

		partOfText.setEndPosition(0);
		assertFalse(partOfText.isValid());
	}
}
