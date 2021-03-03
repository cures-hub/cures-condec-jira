package de.uhd.ifi.se.decision.management.jira.classification;

/**
 * Models the type of machine learning algorithm used to train the binary and
 * fine-grained classifiers. For example, types can be support vector machine or
 * logistic regression.
 */
public enum ClassifierType {

	SVM, // support vector machine
	LR; // logistic regression

}
