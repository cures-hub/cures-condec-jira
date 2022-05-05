package de.uhd.ifi.se.decision.management.jira.classification.preprocessing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uhd.ifi.se.decision.management.jira.classification.FileManager;
import smile.feature.Bag;
import smile.nlp.dictionary.EnglishPunctuations;
import smile.nlp.dictionary.EnglishStopWords;
import smile.nlp.normalizer.SimpleNormalizer;
import smile.nlp.pos.HMMPOSTagger;
import smile.nlp.pos.POSTagger;
import smile.nlp.pos.PennTreebankPOS;
import smile.nlp.stemmer.LancasterStemmer;
import smile.nlp.stemmer.Stemmer;
import smile.nlp.tokenizer.SimpleTokenizer;
import smile.nlp.tokenizer.Tokenizer;

/**
 * Responsible for sentence preprocessing such as tokenization, stemming, word
 * to vector conversion, and nGram generation.
 */
public class Preprocessor {
	private static final Logger LOGGER = LoggerFactory.getLogger(Preprocessor.class);

	public static List<String> PREPROCESSOR_FILE_NAMES = Arrays.asList("glove.6b.50d.csv");

	private Tokenizer tokenizer;
	private Stemmer stemmer;
	private POSTagger partOfSpeechTagger;
	private final PreTrainedGloVe glove;

	private final Integer nGramN;

	private static Preprocessor instance;

	public static Preprocessor getInstance() {
		if (instance == null) {
			instance = new Preprocessor();
		}
		return instance;
	}

	private Preprocessor() {
		LOGGER.info("Init preprocessor for text classification");
		this.nGramN = 3;
		this.glove = new PreTrainedGloVe();
		this.stemmer = new LancasterStemmer();
		this.tokenizer = new SimpleTokenizer(true);
		this.partOfSpeechTagger = HMMPOSTagger.getDefault();
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
		String cleanedSentence = normalize(sentence);
		String[] tokens = tokenize(cleanedSentence);
		double[][] numberTokens = convertToNumbers(tokens);
		double[][] nGrams = generateNGrams(numberTokens, nGramN);
		for (int i = 0; i < nGrams.length; i++) {
			if (nGrams[i] == null) {
				nGrams[i] = new double[numberTokens[0].length * nGramN];
			}
		}

		return nGrams;
	}

	public void bagOfWords(String[] words) {
		Bag bag = new Bag(words);
		bag.toString();
	}

	public String[] getStemmedTokensWithoutStopWords(String sentence) {
		if (sentence == null) {
			return new String[0];
		}
		String cleanedSentence = normalize(sentence);
		// replace long words and possible methods!
		String[] tokens = tokenize(cleanedSentence);
		String[] tokensWithoutStopWords = removeStopWords(tokens);
		return stem(tokensWithoutStopWords);
	}

	public String normalize(String sentence) {
		return SimpleNormalizer.getInstance().normalize(sentence);
	}

	/**
	 * Generates a list of tokenized word from a sentence.
	 *
	 * @param sentence
	 *            as a string.
	 * @return list of word tokens.
	 */
	public String[] tokenize(String sentence) {
		return tokenizer.split(sentence);
	}

	/**
	 * Converts a list of tokens into their stemmed form. What is lemmatisation?
	 * https://en.wikipedia.org/wiki/Lemmatisation E.g.: "better" -> "good".
	 *
	 * @param tokens
	 *            of words to be stemmed.
	 * @return stemmed tokens.
	 */
	public String[] stem(String[] tokens) {
		String[] stemmendTokens = new String[tokens.length];
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
	public double[][] generateNGrams(double[][] tokens, int n) {
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
			gram = concat(gram, tokens[i]);
		return gram;
	}

	/**
	 * @param a
	 *            first array of double values
	 * @param b
	 *            second array of double values
	 * @return concatenated array of double values
	 */
	public static double[] concat(double[] a, double[] b) {
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

	public PennTreebankPOS[] calculatePosTags(List<String> tokens) {
		return partOfSpeechTagger.tag(Arrays.copyOf(tokens.toArray(), tokens.size(), String[].class));
	}

	public String[] removeStopWords(String[] tokens) {
		return Arrays.stream(tokens).filter(word -> !(EnglishStopWords.DEFAULT.contains(word.toLowerCase())
				|| EnglishPunctuations.getInstance().contains(word))).toArray(String[]::new);
	}

	/**
	 * Copies the default preprocessing files to the files in the plugin target.
	 **/
	public static void copyDefaultPreprocessingDataToFile() {
		for (String currentPreprocessingFileName : PREPROCESSOR_FILE_NAMES) {
			FileManager.copyDataToFile(currentPreprocessingFileName);
		}
	}

	/**
	 * Check whether a given text matches any of a list of given regex patterns.
	 * @param text Text to be matched.
	 * @param patterns RegEx patterns.
	 * @return true if at least one pattern matches the given text, otherwise false.
	 */
	private boolean matchesAnyRegEx(String text, String[] patterns) {
		for (String patternString : patterns) {
			Pattern pattern = Pattern.compile(patternString);
			Matcher matcher = pattern.matcher(text);
			if (matcher.matches()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Get noun chunks for a given sentence.
	 *
	 * @param sentence Sentence of which the noun chunks should be retrieved.
	 * @return Noun chunks of the sentence.
	 */
	public String[] getNounChunksForSentence(String sentence) {
		String[] splitAtTags = {"V.*", "IN", "MD"};
		String[] keepWithTags = {"N.*"};
		String[] words = tokenize(sentence);
		String[] posTags = Arrays.stream(calculatePosTags(Arrays.asList(words)))
				.map(PennTreebankPOS::toString)
				.toArray(String[]::new);
		for (String tag: posTags) {
			System.out.println(tag);
		}
		List<String> chunks = new ArrayList<String>();
		StringBuilder currentChunk = new StringBuilder();
		List<String> currentTags = new ArrayList<String>();
		for (int i=0; i < words.length; i++) {
			if (matchesAnyRegEx(posTags[i], splitAtTags)){
				if (currentChunk.length() > 0) {
					for (String currentTag: currentTags) {
						if (matchesAnyRegEx(currentTag, keepWithTags)) {
							chunks.add(currentChunk.toString().strip());
							currentChunk = new StringBuilder();
							currentTags.clear();
							break;
						}
					}
				}
				continue;
			}
			currentChunk.append(" "+words[i]);
			currentTags.add(posTags[i]);
		}
		if (currentChunk.length() > 0) {
			for (String currentTag: currentTags) {
				if (matchesAnyRegEx(currentTag, keepWithTags)) {
					chunks.add(currentChunk.toString().strip());
				}
			}
		}
		return chunks.toArray(String[]::new);
	}

	public PreTrainedGloVe getGlove() {
		return glove;
	}
}
