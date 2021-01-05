package de.uhd.ifi.se.decision.management.jira.classification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
}
