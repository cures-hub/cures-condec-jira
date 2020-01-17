package de.uhd.ifi.se.decision.management.jira.classification;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.util.JiraHome;
import de.uhd.ifi.se.decision.management.jira.classification.implementation.BinaryClassifierImpl;
import de.uhd.ifi.se.decision.management.jira.classification.implementation.FineGrainedClassifierImpl;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Interface to identify decision knowledge in natural language texts using a
 * binary and fine grained supervised classifiers.
 */
public interface DecisionKnowledgeClassifier {

	/**
	 * @issue What is the best place to store the supervised text classifier related
	 * data?
	 * @decision Clone git repo to JIRAHome/data/condec-plugin/classifier!
	 */
	public static final String DEFAULT_DIR = ComponentAccessor.getComponentOfType(JiraHome.class).getDataDirectory()
		.getAbsolutePath() + File.separator + "condec-plugin" + File.separator + "classifier" + File.separator;

	static final Logger LOGGER = LoggerFactory.getLogger(DecisionKnowledgeClassifier.class);

	/**
	 * Determines for a list of strings whether each string is relevant decision
	 * knowledge or not. The classifier needs a list of strings not just one string.
	 *
	 * @param stringsToBeClassified list of strings to be checked for relevance.
	 * @return list of boolean values in the same order as the input strings. Each
	 * value indicates whether a string is relevant (true) or not (false).
	 */
	List<Boolean> makeBinaryPredictions(List<String> stringsToBeClassified);

	/**
	 * Trains a binary classifier using features and labels given by the parameters
	 * of the method.
	 *
	 * @param features features of the instances
	 * @param labels   labels of the instances
	 */
	void trainBinaryClassifier(List<List<Double>> features, List<Integer> labels);

	/**
	 * @param stringsToBeClassified
	 * @return
	 * @see this.makeBinaryDecision
	 */
	List<KnowledgeType> makeFineGrainedPredictions(List<String> stringsToBeClassified);

	/**
	 * @param features
	 * @param labels
	 * @see this.trainBinaryClassifier
	 */
	void trainFineGrainedClassifier(List<List<Double>> features, List<Integer> labels);

	/**
	 * Preprocesses a sentence in such a way, that the classifiers can use them
	 * for training or prediction.
	 *
	 * @param stringsToBePreprocessed sentence
	 * @return preprocessed sentences
	 */
	List<List<Double>> preprocess(String stringsToBePreprocessed);

	/**
	 * Preprocesses sentences in such a way, that the classifiers can use them
	 * for training or prediction. The labels are used when to have the correct labels
	 * for each feature-set.
	 * E.g.: One sentence is preprocessed to multiple N-grams. To get the correct mapping
	 * of features to labels the label list is augmented.
	 *
	 * @param stringsToBePreprocessed sentences
	 * @param labels                  labels of the sentences
	 * @return
	 */
	Map<String, List> preprocess(List<String> stringsToBePreprocessed, List labels);

	BinaryClassifierImpl getBinaryClassifier();

	FineGrainedClassifierImpl getFineGrainedClassifier();

	/**
	 *
	 * @return whether or not the classifier is currently training.
	 */
	default boolean isTraining() {
		return getBinaryClassifier().isCurrentlyTraining() && getFineGrainedClassifier().isCurrentlyTraining();
	}

	/**
	 *
	 * @return whether or not the classifier was trained.
	 */
	default boolean isTrained() {
		return getBinaryClassifier().isModelTrained() && getFineGrainedClassifier().isModelTrained();
	}

}