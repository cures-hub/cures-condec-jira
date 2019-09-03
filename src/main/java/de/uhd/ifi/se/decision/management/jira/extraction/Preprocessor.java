package de.uhd.ifi.se.decision.management.jira.extraction;

import scala.util.matching.Regex;

import java.util.List;
import java.util.regex.Pattern;

public interface Preprocessor {

    public static String URL_PATTERN = "^[a-zA-Z0-9\\-\\.]+\\.(com|org|net|mil|edu|COM|ORG|NET|MIL|EDU)";
    public static String URL_TOKEN = "URL";

    public static String NUMBER_PATTERN = "[+-]?(([0-9]*[.,-:])?[0-9]+)+";
    public static String NUMBER_TOKEN = "NUMBER";

    public static String WHITESPACE_CHARACTERS_PATTERN = "[\\t\\n\\r]+";
    public static String WHITESPACE_CHARACTERS_TOKEN = "";


    /**
     * Generates a List of tokenized word from a sentence.
     *
     * @param sentence
     * @return List of word tokens
     */
    public List<String> tokenize(String sentence);

    /**
     * Replaces unwanted patterns from the String using regular expressions and replacement token.
     * E.g.: removing newline character.
     *
     * @param sentence Sentence that has to be cleaned.
     * @param regex    Regular Expression used to be filter out unwanted parts of text.
     * @param replaceToken Used to replace the matching pattern of the regex.
     * @return Cleaned sentence.
     */
    public String replaceUsingRegEx(String sentence, String regex, String replaceToken);

    /**
     * Converts a list of tokens into their lemmatized form.
     * What is lemmatisation? https://en.wikipedia.org/wiki/Lemmatisation
     * E.g.: "better" -> "good"
     *
     * @param tokens of words to be lemmatized.
     * @return List of lemmatized tokens.
     */
    public List<String> lemmatize(List<String> tokens);

    /**
     * Converts a word-token into its lemmatized form.
     *
     * @param token word to be lemmatized
     * @return lemmatized word-token
     */
    //public String lemmatize(String token);


    /**
     *  Stems a list of word tokens.
     *  What is stemming? https://en.wikipedia.org/wiki/Stemming
     *  E.g.: "running" -> "run"
     *
     * @param tokens to be stemmed
     * @return List of stemmed tokend
     *
     *  Not useful when already using lemmatizer.
     *  public List stem(List<String> tokens);
     */


    /**
     *  Stems a single word token String.
     *
     * @param token to be stemmed
     * @return stemmed word token
     *
     * Not useful when already using lemmatizer.
     * public String stem(String token);
     */

    /**
     * Generates N-Grams from list of tokens.
     * What are N-Grams? https://en.wikipedia.org/wiki/N-gram
     * E.g.: "The cow jumps over the moon". N=3 -> "the cow jumps", "cow jumps over", "jumps over the", "over the moon"
     * (example from: http://text-analytics101.rxnlp.com/2014/11/what-are-n-grams.html)
     *
     * @param tokens tokenized setntence used t generate N-Grams
     * @param N      N-Gram number
     * @return List of N-Grams
     */
    public List generateNGram(List tokens, Integer N);

    /**
     * Converts word tokens to a numerical representation. This is necessary for calculations for the classification.
     *
     * @param tokens List of words in String-representation
     * @return List of words in numerical representation
     */
    public List convertToNumbers(List<String> tokens);

    /**
     * Converts a single word to its numerical representation.
     *
     * @param token word to be converted
     * @return numerical representation of the parameter word
     */
    //public Double convertToNumbers(String token);

    /**
     * This method executes all necessary preprocessing steps.
     *
     * @param sentence to be preprocessed
     * @return N-Gram numerical representation of sentence
     */
    public List preprocess(String sentence);

}
