package de.uhd.ifi.se.decision.management.jira.classification;

import java.io.File;
import java.util.List;

public interface Classifier {
	String DEFAULT_PATH = DecisionKnowledgeClassifier.DEFAULT_DIR +
		File.separator;

	/**
	 * Trains the model using supervised training data, features and labels.
	 *
	 * @param features
	 * @param labels
	 */
	void train(Double[][] features, Integer[] labels) throws AlreadyInTrainingException;

	/**
	 * Trains the model using supervised training data, features and labels.
	 *
	 * @param features
	 * @param labels
	 */
	void train(List<List<Double>> features, List<Integer> labels);

	/**
	 * Trains the model using supervised training data, features and labels.
	 *
	 * @param features
	 * @param label
	 */
	void train(Double[] features, Integer label);

	/**
	 * @param feature
	 * @return probabilities of the labels
	 */
	double[] predictProbabilities(Double[] feature) throws InstantiationError;

	/**
	 * Saves model to a file. So that it can be loaded at a later time.
	 *
	 * @param filePathAndName
	 * @throws Exception
	 */
	void saveToFile(String filePathAndName) throws Exception;

	/**
	 * Saves model to a file. So that it can be loaded at a later time.
	 *
	 * @throws Exception
	 */
	void saveToFile() throws Exception;

	/**
	 * Loads pre-trained model from file.
	 *
	 * @param filePathAndName
	 * @throws Exception
	 */
	boolean loadFromFile(String filePathAndName);

	/**
	 * Loads pre-trained model from file.
	 *
	 * @throws Exception
	 */
	boolean loadFromFile();

	Integer getNumClasses();

	Boolean isCurrentlyTraining();
}
