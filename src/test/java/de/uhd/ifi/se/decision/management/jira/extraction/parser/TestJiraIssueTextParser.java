package de.uhd.ifi.se.decision.management.jira.extraction.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.BreakIterator;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import net.java.ao.test.jdbc.NonTransactional;

public class TestJiraIssueTextParser extends TestSetUp {

	@Before
	public void setUp() {
		init();
	}

	@Test
	@NonTransactional
	public void testCommentWithTwoSentences() {
		List<PartOfJiraIssueText> sentences = JiraIssues
				.getSentencesForCommentText("This is the first sentence. This is the second sentence.");
		assertEquals(2, sentences.size());
	}

	@Test
	@NonTransactional
	public void testCommentWithOneQuote() {
		List<PartOfJiraIssueText> sentences = JiraIssues
				.getSentencesForCommentText("{quote} this is a quote {quote} and this is a test Sentence.");
		assertEquals(2, sentences.size());
	}

	@Test
	@NonTransactional
	public void testCommentWithOneQuoteAtTheBack() {
		List<PartOfJiraIssueText> sentences = JiraIssues
				.getSentencesForCommentText("and this is a test Sentence. {quote} this is a quote {quote} ");
		assertEquals(2, sentences.size());
	}

	@Test
	@NonTransactional
	public void testCommentWithTwoQuotes() {
		List<PartOfJiraIssueText> sentences = JiraIssues.getSentencesForCommentText("{quote} this is a quote {quote} "
				+ "and this is a test Sentence. {quote} this is a second quote {quote} ");
		assertEquals(3, sentences.size());
	}

	@Test
	@NonTransactional
	public void testCommentWithTwoQuotesAndSentenceAfterwards() {
		List<PartOfJiraIssueText> sentences = JiraIssues
				.getSentencesForCommentText("{quote} this is a quote {quote} and this is a test Sentence. "
						+ "{quote} this is a second quote {quote} and a Sentence at the back");
		assertEquals(4, sentences.size());
	}

	@Test
	@NonTransactional
	public void testCommentWithTwoQuotesBehindEachOther() {
		List<PartOfJiraIssueText> sentences = JiraIssues.getSentencesForCommentText("{quote} this is a quote {quote} "
				+ "{quote} this is a second quote right after the first one {quote} and a Sentence at the back");
		assertEquals(3, sentences.size());
	}

	@Test
	@NonTransactional
	public void testSentenceSplitWithNoformats() {
		List<PartOfJiraIssueText> sentences = JiraIssues
				.getSentencesForCommentText("{noformat} this is a noformat {noformat} "
						+ "and this is a test Sentence. {noformat} this is a second noformat {noformat} ");
		assertEquals(3, sentences.size());
	}

	@Test
	@NonTransactional
	public void testSentenceSplitWithNoformatsAndQuotes() {
		List<PartOfJiraIssueText> sentences = JiraIssues
				.getSentencesForCommentText("{noformat} this is a noformat {noformat} and this is a test Sentence. "
						+ "{quote} this is a also a quote {quote}{quote} this is a also a quote {quote} "
						+ "{noformat} this is a noformat {noformat} and this is a test Sentence.");
		assertEquals(6, sentences.size());
	}

	@Test
	@NonTransactional
	public void testSentenceOrder() {
		List<PartOfJiraIssueText> sentences = JiraIssues.getSentencesForCommentText(
				"{noformat} this is a first noformat {noformat} and this is a second test Sentence. "
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
	public void testSentenceSplitWithUnknownTag() {
		List<PartOfJiraIssueText> sentences = JiraIssues.getSentencesForCommentText(
				"{noformat} this is a noformat {noformat} {wuzl} and this is a test Sentence {wuzl}");
		assertEquals(2, sentences.size());
		assertEquals("{noformat} this is a noformat {noformat}", sentences.get(0).getTextWithTags());
		assertEquals(" {wuzl} and this is a test Sentence {wuzl}", sentences.get(1).getTextWithTags());
	}

	@Test
	@NonTransactional
	public void testSentenceSplitWithCodeTag() {
		List<PartOfJiraIssueText> sentences = JiraIssues.getSentencesForCommentText(
				"{code:java} this is a first code {code} and this is a second test Sentence. "
						+ "{quote} this is a also a third quote {quote}"
						+ "{quote} this is a also a fourth quote {quote} "
						+ "{code:java} this is a fifth code {code} and this is a sixth test Sentence.");
		assertEquals(6, sentences.size());
	}

	@Test
	@NonTransactional
	public void testSentenceSplitWithManyDecisionKnowledgeTags() {
		List<PartOfJiraIssueText> sentences = JiraIssues.getSentencesForCommentText(
				"{issue} How to? {issue} {alternative} An alternative could be {alternative}"
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
	public void testRegex() {
		JiraIssueTextParser parser = new JiraIssueTextParser("TEST");

		String text = "Test123 {issue} How to? {issue} {alternative} An alternative could be {alternative}"
				+ "{pro} Great idea! {pro} This is a test! {decision} We will do ...! {decision} {pro} Even better! {pro} {issue} Second issue {issue}"
				+ "{decision}{code:java} public static {code}{decision} And more text! And another question? Yes! {noformat} ... {noformat}";
		List<PartOfJiraIssueText> partsOfText = parser.getPartsOfText(text);

		// assertEquals(10, partsOfText.size());
		assertEquals("Test123", partsOfText.get(0).getDescription());
		assertEquals("{issue} How to? {issue}", partsOfText.get(1).getDescription());
		assertEquals("{alternative} An alternative could be {alternative}", partsOfText.get(2).getDescription());
		assertEquals("{pro} Great idea! {pro}", partsOfText.get(3).getDescription());
		assertEquals("This is a test!", partsOfText.get(4).getDescription());
		assertEquals("{decision} We will do ...! {decision}", partsOfText.get(5).getDescription());
		assertEquals("{pro} Even better! {pro}", partsOfText.get(6).getDescription());
		assertEquals("{issue} Second issue {issue}", partsOfText.get(7).getDescription());
		assertEquals("{decision}{code:java} public static {code}{decision}", partsOfText.get(8).getDescription());
		assertEquals("And more text!", partsOfText.get(9).getDescription());
		assertEquals("And another question?", partsOfText.get(10).getDescription());
		assertEquals("{noformat} ... {noformat}", partsOfText.get(12).getDescription());

		BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
		iterator.setText(text);
		int start = iterator.first();
		for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
			if (end - start > 1 && text.substring(start, end).trim().length() > 0) {
				System.out.println(text.substring(start, end));
			}
		}

	}

}
