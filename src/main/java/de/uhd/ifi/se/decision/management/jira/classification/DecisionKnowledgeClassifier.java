package de.uhd.ifi.se.decision.management.jira.classification;

import java.io.File;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.Preprocessor;

/**
 * Tries to identify decision knowledge in natural language texts using a binary
 * and fine grained supervised classifier.
 */
public class DecisionKnowledgeClassifier {
	private static final Logger LOGGER = LoggerFactory.getLogger(DecisionKnowledgeClassifier.class);

	private BinaryClassifier binaryClassifier;
	private FineGrainedClassifier fineGrainedClassifier;

	/**
	 * @issue What is the best place to store the supervised text classifier related
	 *        data?
	 * @decision Store the data for the text classifier in
	 *           JiraHome/data/condec-plugin/classifier!
	 * @pro Similar as for the git repositories.
	 */
	public static String CLASSIFIER_DIRECTORY = ComponentGetter.PLUGIN_HOME + "classifier" + File.separator;

	public static DecisionKnowledgeClassifier instance;

	private DecisionKnowledgeClassifier() {
		FileManager.copyDefaultTrainingDataToClassifierDirectory();
		Preprocessor.copyDefaultPreprocessingDataToFile();
		loadDefaultBinaryClassifier();
		loadDefaultFineGrainedClassifier();
	}

	public static DecisionKnowledgeClassifier getInstance() {
		if (instance == null) {
			instance = new DecisionKnowledgeClassifier();
		}
		return instance;
	}

	private void loadDefaultBinaryClassifier() {
		this.binaryClassifier = new BinaryClassifier();
		/*
		 * try { //this.binaryClassifier.loadFromFile(); } catch (Exception e) {
		 * LOGGER.error("Could not load a binary classifier from File."); }
		 */
	}

	private void loadDefaultFineGrainedClassifier() {
		this.fineGrainedClassifier = new FineGrainedClassifier(5);
	}

	public static double median(int[] values) {
		// sort array
		Arrays.sort(values);
		// get count of scores
		int totalElements = values.length;
		// check if total number of scores is even
		if (totalElements % 2 == 0) {
			int sumOfMiddleElements = values[totalElements / 2] + values[totalElements / 2 - 1];
			// calculate average of middle elements
			return ((double) sumOfMiddleElements) / 2;
		}
		// get the middle element
		return values[values.length / 2];
	}

	public BinaryClassifier getBinaryClassifier() {
		return binaryClassifier;
	}

	public FineGrainedClassifier getFineGrainedClassifier() {
		return fineGrainedClassifier;
	}

	/**
	 * @return whether or not the classifier is currently training.
	 */
	public boolean isTraining() {
		return this.getFineGrainedClassifier().isCurrentlyTraining()
				|| this.getBinaryClassifier().isCurrentlyTraining();
	}

	/**
	 * @return whether or not the classifier was trained.
	 */
	public boolean isTrained() {
		return getBinaryClassifier().isModelTrained() && getFineGrainedClassifier().isModelTrained();
	}
}
