package de.uhd.ifi.se.decision.management.jira.classification;


import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.Preprocessor;

public class TestPreprocessor extends TestSetUp {
	private static final String testSentence = "The quick brown fox jumps over the lazy dog.";

	private Preprocessor pp;

	@Before
	public void setUp() {
		init();
		pp = Preprocessor.getInstance();
	}

	@Test
	public void testTokenizingWorks() {
		String[] tokenizedTestSentence = { "The", "quick", "brown", "fox", "jumps", "over", "the", "lazy", "dog", "." };
		assertArrayEquals(tokenizedTestSentence, pp.tokenize(testSentence));
	}

	@Test
	public void testStemmingWorksStandalone() {
		String[] sentenceToTokenize = { "The", "quick", "brown", "fox", "jumps", "over", "the", "lazy", "dog", "." };
		String[] tokenizedTestSentence = { "The", "quick", "brown", "fox", "jump", "over", "the", "lazi", "dog", "." };
		assertArrayEquals(tokenizedTestSentence, pp.stem(sentenceToTokenize));
	}

	@Test
	public void testReplaceUsingRegExWorks() {
		assertEquals("I put it on master branch and also linked it in the Marketplace.",
				pp.replaceUsingRegEx("I put it on master branch and also linked it in the Marketplace.\r\n\r\n", Preprocessor.WHITESPACE_CHARACTERS_PATTERN, ""));
	}

	@Test
	public void testPreprocessingWorks() {
		assertEquals(8, pp.preprocess(testSentence).length);
	}

}
