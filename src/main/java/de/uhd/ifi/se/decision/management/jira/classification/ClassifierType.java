package de.uhd.ifi.se.decision.management.jira.classification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Models the type of machine learning algorithm used to train the binary and
 * fine-grained classifiers. For example, types can be support vector machine or
 * logistic regression.
 */
public enum ClassifierType {

	SVM, // support vector machine
	LR; // logistic regression

	private static final Logger LOGGER = LoggerFactory.getLogger(ClassifierType.class);

	public static ClassifierType valueOfOrDefault(String typeName) {
		ClassifierType type = LR;
		try {
			type = valueOf(typeName);
		} catch (Exception e) {
			LOGGER.error(typeName + " is not a valid classifier type. " + e.getMessage());
		}
		return type;
	}

}
