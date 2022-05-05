package de.uhd.ifi.se.decision.management.jira.classification;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;

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
	public void testNormalzingWorks() {
		assertEquals("I put it on master branch and also linked it in the Marketplace.",
				preprocessor.normalize("I put it on master branch and also linked it in the Marketplace.\r\n\r\n"));
	}

	@Test
	public void testGetStemmedTokensWithoutStopWords() {
		String[] stemmedTokens = { "quick", "brown", "fox", "jump", "lazy", "dog" };
		assertArrayEquals(stemmedTokens, preprocessor.getStemmedTokensWithoutStopWords(testSentence));
		assertEquals(0, preprocessor.getStemmedTokensWithoutStopWords(null).length);

		assertEquals("[filt, exerc, frontend]", Arrays.toString(
				preprocessor.getStemmedTokensWithoutStopWords("We could filter the exercises in the frontend")));
		assertEquals("[fil, form, export, play, dat]", Arrays.toString(
				preprocessor.getStemmedTokensWithoutStopWords("In which file format do we export the player data?")));
	}

	@Test
	public void testRemovingStopWordsWorks() {
		String[] stemmedTokens = { "the", "quick", "brown", "fox", "jump", "ov", "the", "lazy", "dog", "" };
		String[] tokensWithoutStopWords = { "quick", "brown", "fox", "jump", "ov", "lazy", "dog", "" };
		assertArrayEquals(tokensWithoutStopWords, preprocessor.removeStopWords(stemmedTokens));
	}

	@Test
	public void testConvertingTokensToNumbers() {
		String[] stemmedTokens = { "the", "quick", "unknownxyzabcsd" };
		double[][] numberRepresentationOfTokens = preprocessor.convertToNumbers(stemmedTokens);
		assertEquals(new double[3][50].length, numberRepresentationOfTokens.length);
		assertEquals(0.418, numberRepresentationOfTokens[0][0], 0.0);
		assertEquals(-0.78581, numberRepresentationOfTokens[0][49], 0.0);
		assertEquals(0.0, numberRepresentationOfTokens[2][49], 0.0);
	}

	@Test
	public void testGetWordVectorForExistingWord() {
		double[] wordVector = preprocessor.getGlove().getWordVector("the");
		assertEquals(50, wordVector.length);
		assertEquals(0.418, wordVector[0], 0.0);
	}

	@Test
	public void testGetWordVectorForUnknownWord() {
		double[] wordVector = preprocessor.getGlove().getWordVector("unknownxyzabcsd");
		assertEquals(50, wordVector.length);
		assertEquals(0, wordVector[0], 0.0);
		assertEquals(0, wordVector[49], 0.0);
	}

	@Test
	public void testGenerateNGrams() {
		double[][] preprocessing = preprocessor.preprocess(testSentence);
		assertEquals(150, preprocessing[0].length);
		assertEquals(10, preprocessing.length);
		assertNotNull(preprocessing[0]);
		assertEquals(0.418, preprocessing[0][0], 0.0);
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

	@Test
	public void testGetNounChunksForSentence() {
		String sentence = "Which database system should we use for the backend?";
		String[] chunks = preprocessor.getNounChunksForSentence(sentence);
		String[] expectedChunks = {"Which database system", "the backend"};
		System.out.println(chunks);
		assertArrayEquals(expectedChunks, chunks);
	}

	@Test
	public void testGetNounChunksForSentenceNoNoun() {
		String sentence = "Asking and asking so much";
		String[] chunks = preprocessor.getNounChunksForSentence(sentence);
		assertEquals(0, chunks.length);
	}
}