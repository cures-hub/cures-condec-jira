package de.uhd.ifi.se.decision.management.jira.classification;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;

/**
 * Interface responsible to train the supervised text classifier. For this
 * purpose, the project admin needs to create and select a file.
 * This abstraction was introduced to allow for different training-file formats.
 */
public interface OnlineTrainer {

	static final Logger LOGGER = LoggerFactory.getLogger(OnlineTrainer.class);

	/**
	 * Trains the Classifier with the Data from the Database that was set and
	 * validated from the user. Creates a new model Files that can be used to
	 * classify the comments  and description of a Jira issue and Git-commit
	 * messages.
	 */
	boolean train();

	/**
	 * Provides a list of decision knowledge element with a knowledge type and a
	 * summary to train the classifier with.
	 *
	 * @param trainingElements list of decision knowledge element with a knowledge type and a
	 *                         summary.
	 */
	void setTrainingData(List<KnowledgeElement> trainingElements);


	/**
	 * Gets the supervised binary and fine grained classifier to identify decision
	 * knowledge in natural language texts.
	 *
	 * @return instance of DecisionKnowledgeClassifier.
	 * @see DecisionKnowledgeClassifier
	 */
	DecisionKnowledgeClassifier getClassifier();


}
