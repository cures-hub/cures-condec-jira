package de.uhd.ifi.se.decision.management.jira.extraction.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.PartOfJiraIssueText;
import net.java.ao.test.jdbc.NonTransactional;

public class TestJiraIssueTextParser extends TestSetUp {

	private JiraIssueTextParser parser;

	@Before
	public void setUp() {
		init();
		parser = new JiraIssueTextParser("TEST");
	}

	@Test
	@NonTransactional
	public void testTwoSentences() {
		List<PartOfJiraIssueText> sentences = parser
				.getPartsOfText("This is the first sentence. This is the second sentence.");
		assertEquals(2, sentences.size());
		assertEquals("This is the first sentence.", sentences.get(0).getDescription());
		assertEquals("This is the second sentence.", sentences.get(1).getDescription());
	}

	@Test
	@NonTransactional
	public void testOneQuoteAndOneSentence() {
		List<PartOfJiraIssueText> sentences = parser
				.getPartsOfText("{quote} this is a quote {quote} and this is a test Sentence.");
		assertEquals(2, sentences.size());
		assertEquals("this is a quote", sentences.get(0).getDescription());
		assertEquals("and this is a test Sentence.", sentences.get(1).getDescription());
	}

	@Test
	@NonTransactional
	public void testOneQuoteAtTheBack() {
		List<PartOfJiraIssueText> sentences = parser
				.getPartsOfText("and this is a test Sentence. {quote} this is a quote {quote} ");
		assertEquals(2, sentences.size());
		assertEquals("and this is a test Sentence.", sentences.get(0).getDescription());
		assertEquals("this is a quote", sentences.get(1).getDescription());
	}

	@Test
	@NonTransactional
	public void testTwoQuotesAndSentenceInBetween() {
		List<PartOfJiraIssueText> sentences = parser.getPartsOfText("{quote} this is a quote {quote} "
				+ "and this is a test Sentence. {quote} this is a second quote {quote} ");
		assertEquals(3, sentences.size());
		assertEquals("this is a quote", sentences.get(0).getDescription());
		assertEquals("and this is a test Sentence.", sentences.get(1).getDescription());
		assertEquals("this is a second quote", sentences.get(2).getDescription());
	}

	@Test
	@NonTransactional
	public void testTwoQuotesAndSentenceAfterwards() {
		List<PartOfJiraIssueText> sentences = parser
				.getPartsOfText("{quote} this is a quote {quote} and this is a test Sentence. "
						+ "{quote} this is a second quote {quote} and a Sentence at the back");
		assertEquals(4, sentences.size());
	}

	@Test
	@NonTransactional
	public void testTwoQuotesBehindEachOther() {
		List<PartOfJiraIssueText> sentences = parser.getPartsOfText("{quote} this is a quote {quote} "
				+ "{quote} this is a second quote right after the first one {quote} and a Sentence at the back");
		assertEquals(3, sentences.size());
	}

	@Test
	@NonTransactional
	public void testNoformats() {
		List<PartOfJiraIssueText> sentences = parser.getPartsOfText("{noformat} this is a noformat {noformat} "
				+ "and this is a test Sentence. {noformat} this is a second noformat {noformat} ");
		assertEquals(3, sentences.size());
	}

	@Test
	@NonTransactional
	public void testNoformatsAndQuotes() {
		List<PartOfJiraIssueText> sentences = parser
				.getPartsOfText("{noformat} this is a noformat {noformat} and this is a test Sentence. "
						+ "{quote} this is a also a quote {quote}{quote} this is a also a quote {quote} "
						+ "{noformat} this is a noformat {noformat} and this is a test Sentence.");
		assertEquals(6, sentences.size());
	}

	@Test
	@NonTransactional
	public void testSentenceOrder() {
		List<PartOfJiraIssueText> sentences = parser
				.getPartsOfText("{noformat} this is a first noformat {noformat} and this is a second test Sentence. "
						+ "{quote} this is a also a third quote {quote}{quote} this is a also a fourth quote {quote} "
						+ "{noformat} this is a fifth noformat {noformat} and this is a sixth test Sentence.");
		assertEquals(6, sentences.size());
		assertTrue(sentences.get(0).getDescription().contains("first"));
		assertTrue(sentences.get(1).getDescription().contains("second"));
		assertTrue(sentences.get(2).getDescription().contains("third"));
		assertTrue(sentences.get(3).getDescription().contains("fourth"));
		assertTrue(sentences.get(4).getDescription().contains("fifth"));
		assertTrue(sentences.get(5).getDescription().contains("sixth"));
	}

	@Test
	@NonTransactional
	public void testUnknownTag() {
		List<PartOfJiraIssueText> sentences = parser
				.getPartsOfText("{noformat} this is a noformat {noformat} {wuzl} and this is a test sentence {wuzl}");
		assertEquals(2, sentences.size());
		assertEquals("this is a noformat", sentences.get(0).getDescription());
		assertEquals("and this is a test sentence", sentences.get(1).getDescription());
	}

	@Test
	@NonTransactional
	public void testCodeTag() {
		List<PartOfJiraIssueText> sentences = parser
				.getPartsOfText("{code:java} this is a first code {code} and this is a second test Sentence. "
						+ "{quote} this is a also a third quote {quote}"
						+ "{quote} this is a also a fourth quote {quote} "
						+ "{code:java} this is a fifth code {code} and this is a sixth test Sentence.");
		assertEquals(6, sentences.size());
		assertEquals("this is a fifth code", sentences.get(4).getDescription());
	}

	@Test
	@NonTransactional
	public void testDecisionKnowledgeTags() {
		List<PartOfJiraIssueText> sentences = parser
				.getPartsOfText("{issue} How to? {issue} {alternative} An alternative could be {alternative}"
						+ "{pro} Great idea! {pro} {decision} We will do ...! {decision} {pro} Even better! {pro}");
		assertEquals(5, sentences.size());
		assertEquals(KnowledgeType.ISSUE, sentences.get(0).getType());
		assertTrue(sentences.get(0).isRelevant());
		assertEquals(KnowledgeType.ALTERNATIVE, sentences.get(1).getType());
		assertTrue(sentences.get(1).isRelevant());
		assertEquals(KnowledgeType.PRO, sentences.get(4).getType());
		assertTrue(sentences.get(4).isRelevant());
	}

	@Test
	@NonTransactional
	public void testDecisionKnowledgeTagsAndOtherMacros() {
		String text = "Some sentence in the front {issue} How to? {issue} {alternative} An alternative could be {alternative}"
				+ "{pro} Great idea! {pro} This is a test! {decision} We will do ...! {decision} {pro} Even better! {pro} {issue} Second issue {issue}"
				+ "{decision}{code:java} public static {code}{decision} And more text! And another question? {noformat} ... {noformat}";
		List<PartOfJiraIssueText> partsOfText = parser.getPartsOfText(text);

		assertEquals(12, partsOfText.size());
		assertEquals("Some sentence in the front", partsOfText.get(0).getDescription());
		assertEquals("How to?", partsOfText.get(1).getDescription());
		assertEquals("An alternative could be", partsOfText.get(2).getDescription());
		assertEquals("Great idea!", partsOfText.get(3).getDescription());
		assertEquals("This is a test!", partsOfText.get(4).getDescription());
		assertEquals("We will do ...!", partsOfText.get(5).getDescription());
		assertEquals("Even better!", partsOfText.get(6).getDescription());
		assertEquals("Second issue", partsOfText.get(7).getDescription());
		assertEquals("public static", partsOfText.get(8).getDescription());
		assertEquals("And more text!", partsOfText.get(9).getDescription());
		assertEquals("And another question?", partsOfText.get(10).getDescription());
		assertEquals("...", partsOfText.get(11).getDescription());
	}
}