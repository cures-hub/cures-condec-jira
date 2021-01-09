package de.uhd.ifi.se.decision.management.jira.classification;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import smile.classification.Classifier;

public abstract class AbstractClassifier {

	protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractClassifier.class);

	protected Classifier<double[]> model;
	private int numClasses;
	private boolean currentlyTraining;

	public AbstractClassifier(int numClasses) {
		this.numClasses = numClasses;
		this.currentlyTraining = false;
	}

	public abstract String getName();

	/**
	 * Trains the model using supervised training data, features and labels.
	 *
	 * @param trainingSamples
	 * @param trainingLabels
	 */
	public abstract void train(double[][] trainingSamples, int[] trainingLabels);

	/**
	 * Trains the model using supervised training data, features and labels.
	 *
	 * @param trainingSample
	 * @param trainingLabel
	 */
	public void update(double[] trainingSample, int trainingLabel) {
		// model.update(trainingSample, trainingLabel);
	}

	/**
	 * @param sample
	 * @return probabilities of the labels
	 */
	public int predict(double[] sample) {
		return model.predict(sample);
	}

	/**
	 * Saves model to a file, so that it can be loaded at a later time.
	 *
	 * @param filePathAndName
	 * @throws Exception
	 */
	public File saveToFile(String filePathAndName) {
		try {
			FileOutputStream fileOut = new FileOutputStream(filePathAndName);
			ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
			objectOut.writeObject(model);
			objectOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new File(filePathAndName);
	}

	/**
	 * Saves model to a file. So that it can be loaded at a later time.
	 */
	public File saveToFile() {
		return saveToFile(DecisionKnowledgeClassifier.CLASSIFIER_DIRECTORY + getName());
	}

	/**
	 * Loads pre-trained model from file.
	 *
	 * @param filePathAndName
	 */
	@SuppressWarnings("unchecked")
	public boolean loadFromFile(String filePathAndName) {
		Classifier<double[]> oldModel = model;
		try {
			FileInputStream fileIn = new FileInputStream(filePathAndName);
			ObjectInputStream objectIn = new ObjectInputStream(fileIn);
			model = (Classifier<double[]>) objectIn.readObject();
			objectIn.close();
		} catch (Exception e) {
			LOGGER.error("Could not load a classifier from file: " + getName() + ". " + e.getMessage());
			model = oldModel;
			return false;
		}
		return true;
	}

	/**
	 * Loads pre-trained model from file.
	 */
	public boolean loadFromFile() {
		return loadFromFile(DecisionKnowledgeClassifier.CLASSIFIER_DIRECTORY + getName());
	}

	public boolean isModelTrained() {
		return model != null;
	}

	public int getNumClasses() {
		return numClasses;
	}

	public boolean isCurrentlyTraining() {
		return currentlyTraining;
	}
}
