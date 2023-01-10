package de.uhd.ifi.se.decision.management.jira.classification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Models the type of machine learning algorithm used to train the binary and
 * fine-grained classifiers. For example, types can be support vector machine or
 * logistic regression.
 * 
 * @issue Which machine learning algorithm should we implement for automatic
 *        text classification?
 * @decision Implement support vector machines for automatic text
 *           classification!
 * @pro Performs best in related work, e.g. by Li et al. (2020): Automatic
 *      Identification of Decisions from the Hibernate Developer Mailing List
 * @decision Implement logistic regression for automatic text classification!
 * @pro Easy to implement.
 * @pro Training is fast.
 * @alternative In the future, more algorithms need to be added, in particular
 *              Naive Bayes and Decision Tree, because they performed well in
 *              related work.
 */
public enum ClassifierType {

	SVM, // support vector machine
	LR, // logistic regression
	NB, // naive bayes
	MLP; // multilayer perceptron neural network

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
