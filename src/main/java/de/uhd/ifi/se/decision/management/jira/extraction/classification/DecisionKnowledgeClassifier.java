package de.uhd.ifi.se.decision.management.jira.extraction.classification;

import java.util.List;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import meka.classifiers.multilabel.LC;
import weka.classifiers.meta.FilteredClassifier;

/**
 * Interface to identify decision knowledge in natural language texts using a binary
 * and fine grained supervised classifiers.
 */
public interface DecisionKnowledgeClassifier {

	List<Boolean> makeBinaryPredictions(List<String> stringsToBeClassified);

	List<KnowledgeType> makeFineGrainedPredictions(List<String> stringsToBeClassified);

	void setFineGrainedClassifier(LC fineGrainedClassifier);

	void setBinaryClassifier(FilteredClassifier binaryClassifier);

}