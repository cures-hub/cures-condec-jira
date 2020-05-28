package de.uhd.ifi.se.decision.management.jira.classification.preprocessing;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.util.JiraHome;
import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.classification.DecisionKnowledgeClassifier;
import de.uhd.ifi.se.decision.management.jira.classification.FileTrainer;
import opennlp.tools.lemmatizer.DictionaryLemmatizer;
import opennlp.tools.lemmatizer.Lemmatizer;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Preprocessor {

	public static String DEFAULT_DIR = ComponentAccessor.getComponentOfType(JiraHome.class).getDataDirectory()
			.getAbsolutePath() + File.separator + "condec-plugin" + File.separator + "classifier" + File.separator;

	public static List<String> PREPROCESSOR_FILE_NAMES = Arrays.asList("token.bin", "pos.bin", "lemmatizer.dict",
			"glove.6b.50d.csv");
	public static String URL_PATTERN = "^[a-zA-Z0-9\\-\\.]+\\.(com|org|net|mil|edu|COM|ORG|NET|MIL|EDU)";
	public static String URL_TOKEN = "URL";

	public static String NUMBER_PATTERN = "[+-]?(([0-9]*[.,-:])?[0-9]+)+";
	public static String NUMBER_TOKEN = "NUMBER";

	public static String WHITESPACE_CHARACTERS_PATTERN = "[\\t\\n\\r]+";
	public static String WHITESPACE_CHARACTERS_TOKEN = "";

	private Tokenizer tokenizer;
	private Lemmatizer lemmatizer;
	private POSTaggerME tagger;
	private PreTrainedGloveSingleton glove;
	private String[] posTags;
	// private NameFinderME nameFinder;
	private Integer nGramN;
	private List<String> tokens;

	public Preprocessor() {
		this.nGramN = 3;
		this.glove = PreTrainedGloveSingleton.getInstance();

		initFiles();
	}

	private void initFiles() {
		Preprocessor.copyDefaultPreprocessingDataToFile();

		File lemmatizerFile = new File(Preprocessor.DEFAULT_DIR + "lemmatizer.dict");
		File tokenizerFile = new File(Preprocessor.DEFAULT_DIR + "token.bin");
		File posFile = new File(Preprocessor.DEFAULT_DIR + "pos.bin");
		try {
			if (!lemmatizerFile.exists()) {
				return;
			}
			InputStream lemmatizerModelIn = new FileInputStream(lemmatizerFile);
			this.lemmatizer = new DictionaryLemmatizer(lemmatizerModelIn);
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
			e.printStackTrace();
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
	 * Converts a list of tokens into their lemmatized form. What is lemmatisation?
	 * https://en.wikipedia.org/wiki/Lemmatisation E.g.: "better" -> "good".
	 *
	 * @param tokens
	 *            of words to be lemmatized.
	 * @return List of lemmatized tokens.
	 */
	public List<String> lemmatize(List<String> tokens) {
		try {
			List<String> lemmatizedTokens = Arrays
					.asList(this.lemmatizer.lemmatize(tokens.toArray(new String[tokens.size()]), this.posTags));
			// Unknown words are replaced by "O" by the lemmatizer.
			// To give the methode for mapping words to numbers a
			// chance we leave unknown words as is.
			// e.g.: tokens: {"jira" "does" "not" "work"}
			// --lemmatize--> {"O" "do" "not" "work"}
			// --next line returns--> {"jira" "do" "not" "work"}
			return IntStream.range(0, lemmatizedTokens.size())
					.mapToObj(i -> lemmatizedTokens.get(i).equals("O") ? tokens.get(i) : lemmatizedTokens.get(i))
					.collect(Collectors.toList());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
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
	public List<List<Double>> generateNGram(List<List<Double>> tokens, Integer N) {
		List<List<Double>> nGrams = new ArrayList<>();
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
	private List<Double> concat(List<List<Double>> tokens, int start, int end) {
		List<Double> gram = new ArrayList<>();
		for (int i = start; i < end; i++)
			gram.addAll(tokens.get(i));
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
	public List<List<Double>> convertToNumbers(List<String> tokens) {
		List<List<Double>> numberTokens = new ArrayList<>();
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
	public synchronized List<List<Double>> preprocess(String sentence) throws Exception {
		try {
			String cleaned_sentence = this.replaceUsingRegEx(sentence, NUMBER_PATTERN, NUMBER_TOKEN.toLowerCase());
			cleaned_sentence = this.replaceUsingRegEx(cleaned_sentence, URL_PATTERN, URL_TOKEN.toLowerCase());
			cleaned_sentence = this.replaceUsingRegEx(cleaned_sentence, WHITESPACE_CHARACTERS_PATTERN,
					WHITESPACE_CHARACTERS_TOKEN.toLowerCase());
			// replace long words and possible methods!
			cleaned_sentence = cleaned_sentence.toLowerCase();
			List<String> tokens = this.tokenize(cleaned_sentence);

			this.posTags = this.calculatePosTags(tokens);

			/*
			 * TODO: if time is sufficient Span[] spans = this.nameFinder.find((String[])
			 * tokens.toArray()); for (Span span : spans) { span.getType();
			 * 
			 * } this.nameFinder.clearAdaptiveData();
			 */

			this.tokens = this.lemmatize(tokens);

			List<List<Double>> numberTokens = this.convertToNumbers(tokens);

			return this.generateNGram(numberTokens, this.nGramN);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			initFiles();
			throw new FileNotFoundException(e.getMessage());
		}
	}

	public void setPosTags(String[] posTags) {
		this.posTags = posTags;
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

	public List<String> getTokens() {
		return this.tokens;
	}
}
