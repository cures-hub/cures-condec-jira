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

/**
 * Abstract superclass for the {@link BinaryClassifier} and
 * {@link FineGrainedClassifier}.
 */
public abstract class AbstractClassifier {

	protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractClassifier.class);

	protected Classifier<double[]> model;
	private int numClasses;
	protected boolean isCurrentlyTraining;

	public AbstractClassifier(int numClasses) {
		this.numClasses = numClasses;
		this.isCurrentlyTraining = false;
	}

	/**
	 * @return name of the classifier that is used as the file name.
	 */
	public abstract String getName();

	/**
	 * Trains the model using supervised training data, features and labels.
	 *
	 * @param trainingData
	 */
	public abstract void train(TrainingData trainingData);

	/**
	 * Updates the classifier using supervised training data.
	 *
	 * @param trainingSample
	 * @param trainingLabel
	 */
	public void update(double[] trainingSample, int trainingLabel) {
		isCurrentlyTraining = true;
		LOGGER.info(
				"Online training is currently not supported for SVMs in SMILE (Statistical Machine Intelligence and Learning Engine) library.");
		// model.update(trainingSample, trainingLabel);
		isCurrentlyTraining = false;
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
			LOGGER.error(filePathAndName + " could not be saved. " + e.getMessage());
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
	 * Loads pre-trained classifier from file.
	 *
	 * @param filePathAndName
	 *            absolute path to the classifier file that should be loaded.
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
	 * Loads pre-trained classifier from file.
	 */
	public boolean loadFromFile() {
		return loadFromFile(DecisionKnowledgeClassifier.CLASSIFIER_DIRECTORY + getName());
	}

	/**
	 * @return true if the classifier was trained.
	 */
	public boolean isTrained() {
		return model != null;
	}

	/**
	 * @return 2 for the binary classifier and > 2 for the fine grained classifier.
	 */
	public int getNumClasses() {
		return numClasses;
	}

	/**
	 * @return true if the classifier is currently training.
	 */
	public boolean isCurrentlyTraining() {
		return isCurrentlyTraining;
	}
}
