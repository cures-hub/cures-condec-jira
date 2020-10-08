package de.uhd.ifi.se.decision.management.jira.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.issue.Issue;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestPartOfJiraIssueText extends TestSetUp {

	PartOfJiraIssueText irrelevantFirstSentence;
	PartOfJiraIssueText issue;
	PartOfJiraIssueText alternative;
	PartOfJiraIssueText proMarkedWithIcon;
	PartOfJiraIssueText code;

	@Before
	public void setUp() {
		init();
		List<PartOfJiraIssueText> partsOfText = JiraIssues
				.getSentencesForCommentText("some sentence in front. {issue} How to do...? {issue}"
						+ "{Alternative} This is an alternative. {Alternative} (y) this is a pro-argument. "
						+ "{code:Java} int i = 0 {code} some sentence in the back.");
		irrelevantFirstSentence = partsOfText.get(0);
		issue = partsOfText.get(1);
		alternative = partsOfText.get(2);
		proMarkedWithIcon = partsOfText.get(3);
		code = partsOfText.get(4);
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
	public void testGetCreationDate() {
		PartOfJiraIssueText sentence = new PartOfJiraIssueText();
		sentence.setCreationDate(new Date());
		assertNotNull(sentence.getCreationDate());
	}

	@Test
	@NonTransactional
	public void testGetTextFromComment() {
		assertEquals(irrelevantFirstSentence.getText(), "some sentence in front. ");
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
		assertTrue(irrelevantFirstSentence.isPlainText());
	}

	@Test
	@NonTransactional
	public void testIsPlainTextCode() {
		assertFalse(code.isPlainText());
	}

	@Test
	@NonTransactional
	public void testIsPlainTextAlternative() {
		assertTrue(alternative.isPlainText());
	}

	@Test
	@NonTransactional
	public void testIsPlainTextIcon() {
		assertTrue(proMarkedWithIcon.isPlainText());
	}

	@Test
	@NonTransactional
	public void testIsRelevantText() {
		assertFalse(irrelevantFirstSentence.isRelevant());
	}

	@Test
	@NonTransactional
	public void testIsRelevantCode() {
		assertFalse(code.isRelevant());
	}

	@Test
	@NonTransactional
	public void testIsRelevantAlternative() {
		assertTrue(alternative.isRelevant());
	}

	@Test
	@NonTransactional
	public void testIsRelevantIcon() {
		assertTrue(proMarkedWithIcon.isRelevant());
	}

	@Test
	@NonTransactional
	public void testIsValidatedText() {
		assertFalse(irrelevantFirstSentence.isValidated());
	}

	@Test
	@NonTransactional
	public void testIsValidatedCode() {
		assertFalse(code.isValidated());
	}

	@Test
	@NonTransactional
	public void testIsValidatedAlternative() {
		assertTrue(alternative.isValidated());
	}

	@Test
	@NonTransactional
	public void testIsValidatedIssue() {
		assertTrue(issue.isValidated());
	}

	@Test
	@NonTransactional
	public void testIsValidatedIcon() {
		assertTrue(proMarkedWithIcon.isValidated());
	}

	@Test
	@NonTransactional
	public void testGetLength() {
		assertEquals(29, proMarkedWithIcon.getLength());
	}

	@Test
	@NonTransactional
	public void testGetJiraIssue() {
		PartOfJiraIssueText partOfText = new PartOfJiraIssueText();
		partOfText.setJiraIssue(1);
		Issue jiraIssue = partOfText.getJiraIssue();
		assertNotNull(jiraIssue);
		assertEquals(1, (long) jiraIssue.getId());
	}

	@Test
	@NonTransactional
	public void testGetCreatorOfComment() {
		assertEquals(JiraUsers.SYS_ADMIN.getApplicationUser(), alternative.getCreator());
	}

	@Test
	@NonTransactional
	public void testGetCreatorOfDescription() {
		// comment id 0 means that the element is documented in the description
		alternative.setCommentId(0);
		assertEquals(JiraUsers.SYS_ADMIN.getApplicationUser(), alternative.getCreator());
	}

	@Test
	@NonTransactional
	public void testGetCreatorInValidJiraIssue() {
		// comment id 0 means that the element is documented in the description
		alternative.setCommentId(0);
		alternative.setJiraIssue(null);
		assertFalse(alternative.isValid());
		assertNull(alternative.getCreator());
	}

	@Test
	@NonTransactional
	public void testIsValid() {
		assertTrue(alternative.isValid());

		// this means that the element is documented in the description
		alternative.setCommentId(0);
		assertTrue(alternative.isValid());

		alternative.setEndPosition(0);
		assertFalse(alternative.isValid());
	}

	@Test
	@NonTransactional
	public void testGetKeyValidJiraIssue() {
		assertEquals("TEST-30:3", alternative.getKey());
	}

	@Test
	@NonTransactional
	public void testGetKeyInvalidJiraIssue() {
		alternative.setJiraIssue(null);
		assertFalse(alternative.isValid());
		assertEquals("", alternative.getKey());
	}
}
