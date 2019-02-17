package de.uhd.ifi.se.decision.management.jira.extraction;

import java.util.List;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import meka.classifiers.multilabel.LC;
import weka.classifiers.meta.FilteredClassifier;

/**
 * Interface to identify decision knowledge in natural language texts using a
 * binary and fine grained supervised classifiers.
 */
public interface DecisionKnowledgeClassifier {

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
}