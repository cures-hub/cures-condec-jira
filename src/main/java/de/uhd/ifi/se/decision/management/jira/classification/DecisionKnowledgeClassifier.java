package de.uhd.ifi.se.decision.management.jira.classification;

import java.io.File;
import java.util.List;
import java.util.Map;

import de.uhd.ifi.se.decision.management.jira.classification.implementation.BinaryClassifierImplementation;
import de.uhd.ifi.se.decision.management.jira.classification.implementation.FineGrainedClassifierImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	static final Logger LOGGER = LoggerFactory.getLogger(DecisionKnowledgeClassifier.class);

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

	void trainBinaryClassifier(Double[][] features, Integer[] labels);

	void updateBinaryClassifier(Double[] feature, Integer label);

	void trainBinaryClassifier(List<List<Double>> features, List<Integer> labels);


	List<KnowledgeType> makeFineGrainedPredictions(List<String> stringsToBeClassified);

	void trainFineGrainedClassifier(Double[][] features, Integer[] labels);

	void trainFineGrainedClassifier(List<List<Double>> features, List<Integer> labels);


	void updateFineGrainedClassifier(Double[] feature, Integer label);

	List<List<Double>> preprocess(String stringsToBePreprocessed);

	List<List<Double>> preprocess(List<String> stringsToBePreprocessed);

	Map<String, List> preprocess(List<String> stringsToBePreprocessed, List labels);



	/**
	 * Set the classifier for binary prediction.
	 * 
	 * @param binaryClassifier
	 *            classifier for binary prediction.
	 */
	void setBinaryClassifier(BinaryClassifierImplementation binaryClassifier);

	BinaryClassifierImplementation getBinaryClassifier();

	/**
	 * Set the classifier for fine grained prediction.
	 *
	 * @param fineGrainedClassifier
	 *            classifier for fine grained prediction.
	 */
	void setFineGrainedClassifier(FineGrainedClassifierImpl fineGrainedClassifier);

	FineGrainedClassifierImpl getFineGrainedClassifier();

}