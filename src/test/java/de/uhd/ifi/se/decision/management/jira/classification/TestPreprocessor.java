package de.uhd.ifi.se.decision.management.jira.classification;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.Preprocessor;

public class TestPreprocessor extends TestSetUp {
	private static final Logger LOGGER = LoggerFactory.getLogger(TestPreprocessor.class);

	private static final String testSentence = "The quick brown fox jumps over the lazy dog.";

	private Preprocessor pp;

	@Before
	public void setUp() {
		init();
		pp = Preprocessor.getInstance();
	}

	@Test
	public void testTokenizingWorks() {
		List<String> tokenizedTestSentence = Arrays.asList("The", "quick", "brown", "fox", "jumps", "over", "the", "lazy", "dog", ".");
		assertEquals(pp.tokenize(TestPreprocessor.testSentence), tokenizedTestSentence);
	}

	@Test
	public void testStemmingWorksStandalone() {
		List<String> tokenizedTestSentence = Arrays.asList("The", "quick", "brown", "fox", "jumps", "over", "the", "lazy", "dog", ".");
		assertEquals(Arrays.asList("The", "quick", "brown", "fox", "jump", "over", "the", "lazi", "dog", "."),
				pp.stem(tokenizedTestSentence));
	}

	@Test
	public void testReplaceUsingRegExWorks() {
		assertEquals("I put it on master branch and also linked it in the Marketplace.",
				pp.replaceUsingRegEx("I put it on master branch and also linked it in the Marketplace.\r\n\r\n", Preprocessor.WHITESPACE_CHARACTERS_PATTERN, ""));
	}

	@Test

	public void testPreprocessingWorks() {

		int testTokenizedSize = 0;
		try {
			testTokenizedSize = pp.preprocess(TestPreprocessor.testSentence).size();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		assertEquals(testTokenizedSize, 8);
	}

}
