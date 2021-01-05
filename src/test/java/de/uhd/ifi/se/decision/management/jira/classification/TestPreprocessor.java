package de.uhd.ifi.se.decision.management.jira.classification;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.Preprocessor;

public class TestPreprocessor extends TestSetUp {

	private static final String testSentence = "The quick brown fox jumps over the lazy dog.";
	private Preprocessor preprocessor;

	@Before
	public void setUp() {
		init();
		preprocessor = Preprocessor.getInstance();
	}

	@Test
	public void testTokenizingWorks() {
		String[] tokens = { "The", "quick", "brown", "fox", "jumps", "over", "the", "lazy", "dog", "." };
		assertArrayEquals(tokens, preprocessor.tokenize(testSentence));
	}

	@Test
	public void testStemmingWorks() {
		String[] tokensNotStemmed = { "The", "quick", "brown", "fox", "jumps", "over", "the", "lazy", "dog", "." };
		String[] stemmedTokens = { "the", "quick", "brown", "fox", "jump", "ov", "the", "lazy", "dog", "" };
		assertArrayEquals(stemmedTokens, preprocessor.stem(tokensNotStemmed));
	}

	@Test
	public void testReplaceUsingRegExWorks() {
		assertEquals("I put it on master branch and also linked it in the Marketplace.",
				preprocessor.replaceUsingRegEx(
						"I put it on master branch and also linked it in the Marketplace.\r\n\r\n",
						Preprocessor.WHITESPACE_CHARACTERS_PATTERN, ""));
	}

	@Test
	public void testGetStemmedTokens() {
		preprocessor.preprocess(testSentence);
		String[] stemmedTokens = { "the", "quick", "brown", "fox", "jump", "ov", "the", "lazy", "dog", "" };
		assertArrayEquals(stemmedTokens, preprocessor.getStemmedTokens());
	}

	@Test
	public void testConvertingTokensToNumbers() {
		String[] stemmedTokens = { "the", "quick", "unknownxyzabcsd" };
		double[][] numberRepresentationOfTokens = preprocessor.convertToNumbers(stemmedTokens);
		assertEquals(new double[3][50].length, numberRepresentationOfTokens.length);
		assertEquals(0.418, numberRepresentationOfTokens[0][0]);
		assertEquals(-0.78581, numberRepresentationOfTokens[0][49]);
		assertEquals(0.0, numberRepresentationOfTokens[2][49]);
	}

	@Test
	public void testGetWordVectorForExistingWord() {
		double[] wordVector = preprocessor.getGlove().getWordVector("the");
		assertEquals(50, wordVector.length);
		assertEquals(0.418, wordVector[0]);
	}

	@Test
	public void testGetWordVectorForUnknownWord() {
		double[] wordVector = preprocessor.getGlove().getWordVector("unknownxyzabcsd");
		assertEquals(50, wordVector.length);
		assertEquals(0, wordVector[0]);
		assertEquals(0, wordVector[49]);
	}

	@Test
	public void testGenerateNGrams() {
		double[][] preprocessing = preprocessor.preprocess(testSentence);
		assertEquals(150, preprocessing[0].length);
		assertEquals(10, preprocessing.length);
		assertNotNull(preprocessing[0]);
		assertEquals(0.418, preprocessing[0][0]);
	}

	@Test
	public void testGenerateNGramsInvalid() {
		double[][] preprocessing = preprocessor.preprocess("Issue");
		assertEquals(1, preprocessing.length);
		assertEquals(150, preprocessing[0].length);
		assertEquals(1, preprocessing.length);
		assertNotNull(preprocessing[0]);
		assertNotNull(preprocessing[0][0]);
	}
}