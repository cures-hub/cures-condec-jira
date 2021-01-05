package de.uhd.ifi.se.decision.management.jira.classification.preprocessing;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uhd.ifi.se.decision.management.jira.classification.DecisionKnowledgeClassifier;
import de.uhd.ifi.se.decision.management.jira.classification.FileManager;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.stemmer.PorterStemmer;
import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

public class Preprocessor {
	private static final Logger LOGGER = LoggerFactory.getLogger(Preprocessor.class);

	public static List<String> PREPROCESSOR_FILE_NAMES = Arrays.asList("token.bin", "pos.bin", "glove.6b.50d.csv");
	public static String URL_PATTERN = "^[a-zA-Z0-9\\-\\.]+\\.(com|org|net|mil|edu|COM|ORG|NET|MIL|EDU)";
	public static String URL_TOKEN = "URL";

	public static String NUMBER_PATTERN = "[+-]?(([0-9]*[.,-:])?[0-9]+)+";
	public static String NUMBER_TOKEN = "NUMBER";

	public static String WHITESPACE_CHARACTERS_PATTERN = "[\\t\\n\\r]+";
	public static String WHITESPACE_CHARACTERS_TOKEN = "";

	private Tokenizer tokenizer;
	private Stemmer stemmer;
	private POSTaggerME tagger;
	private final PreTrainedGloVe glove;

	private final Integer nGramN;
	private CharSequence[] stemmedTokens;

	private static Preprocessor instance;

	public static Preprocessor getInstance() {
		if (instance == null) {
			instance = new Preprocessor();
		}
		return instance;
	}

	private Preprocessor() {
		this.nGramN = 3;
		this.glove = new PreTrainedGloVe();
		if (filesNotInitialized()) {
			initFiles();
		}
	}

	private boolean filesNotInitialized() {
		return this.tokenizer == null || this.stemmer == null || this.tagger == null;
	}

	private void initFiles() {
		Preprocessor.copyDefaultPreprocessingDataToFile();

		File tokenizerFile = new File(DecisionKnowledgeClassifier.CLASSIFIER_DIRECTORY + "token.bin");
		File posFile = new File(DecisionKnowledgeClassifier.CLASSIFIER_DIRECTORY + "pos.bin");
		try {

			this.stemmer = new PorterStemmer();
			// lemmatizerModel = new LemmatizerModel(modelIn);

			if (!tokenizerFile.exists()) {
				return;
			}
			InputStream tokenizerModelIn = new FileInputStream(tokenizerFile);
			TokenizerModel tokenizerModel = new TokenizerModel(tokenizerModelIn);
			this.tokenizer = new TokenizerME(tokenizerModel);

			if (!posFile.exists()) {
				return;
			}
			InputStream posModelIn = new FileInputStream(posFile);
			POSModel posModel = new POSModel(posModelIn);
			this.tagger = new POSTaggerME(posModel);

			// modelIn = new FileInputStream(LANGUAGE_MODEL_PATH + "person.bin");
			// TokenNameFinderModel model = new TokenNameFinderModel(modelIn);
			// this.nameFinder = new NameFinderME(model);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	/**
	 * Generates a list of tokenized word from a sentence.
	 *
	 * @param sentence
	 *            as a string.
	 * @return list of word tokens.
	 */
	public String[] tokenize(String sentence) {
		return tokenizer.tokenize(sentence);
	}

	/**
	 * Replaces unwanted patterns from the String using regular expressions and
	 * replacement token. E.g.: removing newline character.
	 *
	 * @param sentence
	 *            Sentence that has to be cleaned.
	 * @param regex
	 *            Regular Expression used to be filter out unwanted parts of text.
	 * @param replaceToken
	 *            Used to replace the matching pattern of the regex.
	 * @return Cleaned sentence.
	 */
	public String replaceUsingRegEx(String sentence, String regex, String replaceToken) {
		return sentence.replaceAll(regex, replaceToken);
	}

	/**
	 * Converts a list of tokens into their stemmed form. What is lemmatisation?
	 * https://en.wikipedia.org/wiki/Lemmatisation E.g.: "better" -> "good".
	 *
	 * @param tokens
	 *            of words to be stemmed.
	 * @return stemmed tokens.
	 */
	public CharSequence[] stem(String[] tokens) {
		CharSequence[] stemmendTokens = new CharSequence[tokens.length];
		for (int i = 0; i < tokens.length; i++) {
			stemmendTokens[i] = stemmer.stem(tokens[i]);
		}
		return stemmendTokens;
	}

	/**
	 * Generates N-Grams from list of tokens. What are N-Grams?
	 * https://en.wikipedia.org/wiki/N-gram E.g.: "The cow jumps over the moon". N=3
	 * -> "the cow jumps", "cow jumps over", "jumps over the", "over the moon"
	 * (example from:
	 * http://text-analytics101.rxnlp.com/2014/11/what-are-n-grams.html)
	 *
	 * @param tokens
	 *            tokenized sentence used to generate N-Grams
	 * @param n
	 *            N-Gram number
	 * @return array of N-Grams
	 */
	public double[][] generateNGram(double[][] tokens, int n) {
		double[][] nGrams = new double[tokens.length][];
		for (int i = 0; i < tokens.length - n + 1; i++) {
			nGrams[i] = concat(tokens, i, i + n);
		}
		return nGrams;
	}

	/**
	 * Converts word tokens to a numerical representation. This is necessary for
	 * calculations for the classification.
	 *
	 * @param tokens
	 *            List of words in String-representation
	 * @return List of words in numerical representation
	 */
	private double[] concat(double[][] tokens, int start, int end) {
		double[] gram = new double[0];
		for (int i = start; i < end; i++)
			gram = concatenate(gram, tokens[i]);
		return gram;
	}

	/**
	 * @param a
	 *            first array of double values
	 * @param b
	 *            second array of double values
	 * @return concatenated array of double values
	 */
	public static double[] concatenate(double[] a, double[] b) {
		int aLen = a.length;
		int bLen = b.length;
		double[] c = new double[aLen + bLen];
		System.arraycopy(a, 0, c, 0, aLen);
		System.arraycopy(b, 0, c, aLen, bLen);
		return c;
	}

	/**
	 * Converts word tokens to a numerical representation. This is necessary for
	 * calculations for the classification.
	 *
	 * @param tokens
	 *            list of words in String representation.
	 * @return list of words in numerical representation.
	 */
	public double[][] convertToNumbers(String[] tokens) {
		double[][] numberTokens = new double[tokens.length][];
		for (int i = 0; i < tokens.length; i++) {
			numberTokens[i] = glove.getWordVector(tokens[i]);
		}
		return numberTokens;
	}

	public String[] calculatePosTags(List<String> tokens) {
		return tagger.tag(Arrays.copyOf(tokens.toArray(), tokens.size(), String[].class));
	}

	/**
	 * This method executes all necessary preprocessing steps. Preprocesses a
	 * sentence in such a way, that the classifiers can use them for training or
	 * prediction.
	 * 
	 * @param sentence
	 *            to be preprocessed
	 * @return N-Gram numerical representation of sentence (preprocessed sentences)
	 */
	public double[][] preprocess(String sentence) {
		String cleanedSentence = replaceUsingRegEx(sentence, NUMBER_PATTERN, NUMBER_TOKEN.toLowerCase());
		cleanedSentence = replaceUsingRegEx(cleanedSentence, URL_PATTERN, URL_TOKEN.toLowerCase());
		cleanedSentence = replaceUsingRegEx(cleanedSentence, WHITESPACE_CHARACTERS_PATTERN,
				WHITESPACE_CHARACTERS_TOKEN.toLowerCase());
		// replace long words and possible methods!
		cleanedSentence = cleanedSentence.toLowerCase();
		String[] tokens = tokenize(cleanedSentence);
		stemmedTokens = stem(tokens);

		double[][] numberTokens = convertToNumbers(tokens);
		double[][] nGrams = generateNGram(numberTokens, nGramN);
		for (int i = 0; i < nGrams.length; i++) {
			if (nGrams[i] == null) {
				nGrams[i] = new double[numberTokens[0].length * nGramN];
			}
		}

		return nGrams;
	}

	/**
	 * Copies the default preprocessing files to the files in the plugin target.
	 **/
	public static void copyDefaultPreprocessingDataToFile() {
		for (String currentPreprocessingFileName : PREPROCESSOR_FILE_NAMES) {
			FileManager.copyDataToFile(currentPreprocessingFileName);
		}
	}

	public CharSequence[] getStemmedTokens() {
		return stemmedTokens;
	}

	public PreTrainedGloVe getGlove() {
		return glove;
	}
}
