package de.uhd.ifi.se.decision.management.jira.extraction;

import java.io.File;
import java.util.List;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.util.JiraHome;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import meka.classifiers.multilabel.LC;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.tokenizers.NGramTokenizer;
import weka.core.tokenizers.Tokenizer;
import weka.filters.unsupervised.attribute.StringToWordVector;

/**
 * Interface to identify decision knowledge in natural language texts using a
 * binary and fine grained supervised classifiers.
 */
public interface DecisionKnowledgeClassifier {

	/**
	 * @issue What is the best place to store the supervised text classifier related
	 *        data?
	 * @decision Clone git repo to JIRAHome/data/condec-plugin/classifier!
	 */
	public static final String DEFAULT_DIR = ComponentAccessor.getComponentOfType(JiraHome.class).getDataDirectory()
			.getAbsolutePath() + File.separator + "condec-plugin" + File.separator + "classifier" + File.separator;

	/**
	 * Determines for a list of strings whether each string is relevant decision
	 * knowledge or not. The classifier needs a list of strings not just one string.
	 * 
	 * @param stringsToBeClassified
	 *            list of strings to be checked for relevance.
	 * @return list of boolean values in the same order as the input strings. Each
	 *         value indicates whether a string is relevant (true) or not (false).
	 */
	List<Boolean> makeBinaryPredictions(List<String> stringsToBeClassified);

	/**
	 * Determines the knowledge type for a list of strings, respectively. The
	 * classifier needs a list of strings not just one string.
	 * 
	 * @see KnowledgeType
	 * @param stringsToBeClassified
	 *            list of strings that should be classified into knowledge types.
	 * @return list of knowledge types in the same order as the input strings. Each
	 *         value in the list is the knowledge type of the respective string.
	 */
	List<KnowledgeType> makeFineGrainedPredictions(List<String> stringsToBeClassified);

	/**
	 * Set the classifier for binary prediction.
	 * 
	 * @see FilteredClassifier
	 * @param binaryClassifier
	 *            classifier for binary prediction.
	 */
	void setBinaryClassifier(FilteredClassifier binaryClassifier);

	/**
	 * Set the classifier for fine grained prediction.
	 * 
	 * @see LC
	 * @param fineGrainedClassifier
	 *            classifier for fine grained prediction.
	 */
	void setFineGrainedClassifier(LC fineGrainedClassifier);

	/**
	 * Creates a String to Word Vector for the Classifier All Elements are Lowercase
	 * Tokens
	 * 
	 * @return StringToWordVector
	 */
	public static StringToWordVector getStringToWordVector() {
		StringToWordVector stringToWordVector = new StringToWordVector();
		stringToWordVector.setLowerCaseTokens(true);
		stringToWordVector.setIDFTransform(true);
		stringToWordVector.setTFTransform(true);
		stringToWordVector.setTokenizer(getTokenizer());
		stringToWordVector.setWordsToKeep(1000000);
		return stringToWordVector;
	}

	/**
	 * Creates the tokenizer and sets the Values and Options for the String to Word
	 * Vector
	 */
	public static Tokenizer getTokenizer() {
		Tokenizer tokenizer = new NGramTokenizer();
		try {
			String[] options = weka.core.Utils.splitOptions(
					"weka.core.tokenizers.NGramTokenizer -max 3 -min 1 -delimiters \" \\r\\n\\t.,;:\\'\\\"()?!\"");
			tokenizer.setOptions(options);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tokenizer;
	}
}