package de.uhd.ifi.se.decision.management.jira.extraction.classification;


/**
 * Interface to train the classifier manually with the given data from the user.
 */
public interface ClassificationTrainer {

	/**
	 * Trains the Classifier withe the Data from the Database that was set and
	 * validated from the user.
	 */
	void train();

}
