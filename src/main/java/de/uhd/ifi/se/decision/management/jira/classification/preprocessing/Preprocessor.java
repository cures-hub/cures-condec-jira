package de.uhd.ifi.se.decision.management.jira.classification.preprocessing;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.util.JiraHome;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.classification.DecisionKnowledgeClassifier;
import de.uhd.ifi.se.decision.management.jira.classification.FileTrainer;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.stemmer.PorterStemmer;
import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

public class Preprocessor {
	private static final Logger LOGGER = LoggerFactory.getLogger(Preprocessor.class);

	public static String DEFAULT_DIR = ComponentAccessor.getComponentOfType(JiraHome.class).getDataDirectory()
			.getAbsolutePath() + File.separator + "condec-plugin" + File.separator + "classifier" + File.separator;

	public static List<String> PREPROCESSOR_FILE_NAMES = Arrays.asList("token.bin", "pos.bin", // "lemmatizer.dict",
			"glove.6b.50d.csv");
	public static String URL_PATTERN = "^[a-zA-Z0-9\\-\\.]+\\.(com|org|net|mil|edu|COM|ORG|NET|MIL|EDU)";
	public static String URL_TOKEN = "URL";

	public static String NUMBER_PATTERN = "[+-]?(([0-9]*[.,-:])?[0-9]+)+";
	public static String NUMBER_TOKEN = "NUMBER";

	public static String WHITESPACE_CHARACTERS_PATTERN = "[\\t\\n\\r]+";
	public static String WHITESPACE_CHARACTERS_TOKEN = "";

	private Tokenizer tokenizer;
	private Stemmer stemmer;
	private POSTaggerME tagger;
	private final PreTrainedGloveSingleton glove;
	// private NameFinderME nameFinder;
	private final Integer nGramN;
	private List<CharSequence> tokens;

	private static Preprocessor instance;

	public static Preprocessor getInstance() {
		if (instance == null) {
			instance = new Preprocessor();
		}
		return instance;
	}

	private Preprocessor() {
		this.nGramN = 3;
		this.glove = PreTrainedGloveSingleton.getInstance();

		if (filesNotInitialized()) {
			initFiles();
		}
	}

	private boolean filesNotInitialized() {
		return this.tokenizer == null || this.stemmer == null || this.tagger == null;
	}

	private void initFiles() {
		Preprocessor.copyDefaultPreprocessingDataToFile();

		File tokenizerFile = new File(Preprocessor.DEFAULT_DIR + "token.bin");
		File posFile = new File(Preprocessor.DEFAULT_DIR + "pos.bin");
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
	public List<String> tokenize(String sentence) {
		return Arrays.asList(this.tokenizer.tokenize(sentence));
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
	 * @return List of stemmed tokens.
	 */
	public List<CharSequence> stem(List<String> tokens) {
		return tokens.stream().map((token) -> stemmer.stem(token)).collect(Collectors.toList());

	}

	/**
	 * Generates N-Grams from list of tokens. What are N-Grams?
	 * https://en.wikipedia.org/wiki/N-gram E.g.: "The cow jumps over the moon". N=3
	 * -> "the cow jumps", "cow jumps over", "jumps over the", "over the moon"
	 * (example from:
	 * http://text-analytics101.rxnlp.com/2014/11/what-are-n-grams.html)
	 *
	 * @param tokens
	 *            tokenized setntence used t generate N-Grams
	 * @param N
	 *            N-Gram number
	 * @return List of N-Grams
	 */
	public List<double[]> generateNGram(List<double[]> tokens, Integer N) {
		List<double[]> nGrams = new ArrayList<>();
		for (int i = 0; i < tokens.size() - N + 1; i++)
			nGrams.add(concat(tokens, i, i + N));
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
	private double[] concat(List<double[]> tokens, int start, int end) {
		double[] gram = new double[end - start];
		for (int i = start; i < end; i++)
			gram = tokens.get(i);
		return gram;
	}

	/**
	 * Converts word tokens to a numerical representation. This is necessary for
	 * calculations for the classification.
	 *
	 * @param tokens
	 *            List of words in String-representation
	 * @return list of words in numerical representation
	 */
	public List<double[]> convertToNumbers(List<String> tokens) {
		List<double[]> numberTokens = new ArrayList<>();
		for (String wordToken : tokens) {
			numberTokens.add(glove.getWordVector(wordToken));
		}
		return numberTokens;
	}

	public String[] calculatePosTags(List<String> tokens) {
		return this.tagger.tag(Arrays.copyOf(tokens.toArray(), tokens.size(), String[].class));
	}

	/**
	 * This method executes all necessary preprocessing steps.
	 *
	 * @param sentence
	 *            to be preprocessed
	 * @return N-Gram numerical representation of sentence
	 */
	public synchronized List<double[]> preprocess(String sentence) {
		try {
			String cleaned_sentence = this.replaceUsingRegEx(sentence, NUMBER_PATTERN, NUMBER_TOKEN.toLowerCase());
			cleaned_sentence = this.replaceUsingRegEx(cleaned_sentence, URL_PATTERN, URL_TOKEN.toLowerCase());
			cleaned_sentence = this.replaceUsingRegEx(cleaned_sentence, WHITESPACE_CHARACTERS_PATTERN,
					WHITESPACE_CHARACTERS_TOKEN.toLowerCase());
			// replace long words and possible methods!
			cleaned_sentence = cleaned_sentence.toLowerCase();
			List<String> tokens = this.tokenize(cleaned_sentence);

			/*
			 * TODO: if time is sufficient Span[] spans = this.nameFinder.find((String[])
			 * tokens.toArray()); for (Span span : spans) { span.getType();
			 *
			 * } this.nameFinder.clearAdaptiveData();
			 */

			this.tokens = this.stem(tokens);

			List<double[]> numberTokens = this.convertToNumbers(tokens);

			return this.generateNGram(numberTokens, this.nGramN);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return new ArrayList<>();
	}

	/**
	 * Copies the default preprocessing files to the files in the plugin target.
	 **/
	public static void copyDefaultPreprocessingDataToFile() {
		for (String currentPreprocessingFileName : PREPROCESSOR_FILE_NAMES) {
			FileTrainer.copyDataToFile(DecisionKnowledgeClassifier.DEFAULT_DIR, currentPreprocessingFileName,
					ComponentGetter.getUrlOfClassifierFolder());
		}
	}

	public List<CharSequence> getTokens() {
		return this.tokens;
	}
}
