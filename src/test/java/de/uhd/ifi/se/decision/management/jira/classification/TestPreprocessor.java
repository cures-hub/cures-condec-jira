package de.uhd.ifi.se.decision.management.jira.classification;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.Preprocessor;
import smile.feature.Bag;
import smile.math.MathEx;
import smile.nlp.dictionary.EnglishPunctuations;
import smile.nlp.dictionary.EnglishStopWords;

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

	@Test
	public void testBagOfWords() {
		String[] words = preprocessor
				.getStemmedTokensWithoutStopWords(
						"The quick brown fox jumps over the lazy dog.");
		Bag bag = new Bag(words);
		String[] tokens = { "quick", "brown", "dog" };
		System.out.print(Arrays.toString(bag.apply(tokens)));
	}

	@Test
	public void testTDFIDF() {
		TrainingData trainingData = new TrainingData();
		String[] sentences = trainingData.sentences;
		String[] words = Arrays.stream(sentences).flatMap(s -> Arrays.stream(preprocessor.tokenize(s)))
				.filter(w -> !(EnglishStopWords.DEFAULT.contains(w.toLowerCase())
						|| EnglishPunctuations.getInstance().contains(w)))
				.toArray(String[]::new);

		words = preprocessor.stem(words);

		Map<String, Integer> bags = Arrays.stream(words).map(String::toLowerCase)
				.collect(Collectors.groupingBy(Function.identity(), Collectors.summingInt(e -> 1)));

		String[] features = { "like", "good", "perform", "littl", "love", "bad", "best" };

		double[] x = new double[features.length];
		int zero = Integer.valueOf(0);
		for (int i = 0; i < x.length; i++) {
			x[i] = bags.getOrDefault(features[i], zero);
		}

		double[] y = new double[x.length];
		int[] df = new int[features.length];

		for (int i = 0; i < x.length; i++) {
			double maxtf = MathEx.max(x);
			x[i] = (x[i] / maxtf) * Math.log((1.0 + 1) / (1.0 + df[i]));
		}

		MathEx.unitize(x);

	}
}