package de.uhd.ifi.se.decision.management.jira.classification;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
	protected int numClasses;
	protected boolean isCurrentlyTraining;

	/**
	 * Reads the classifier from file if it was already trained and saved to file
	 * system.
	 * 
	 * @param numClasses
	 *            2 for the binary classifier and > 2 for the fine grained
	 *            classifier.
	 */
	public AbstractClassifier(int numClasses) {
		this.numClasses = numClasses;
		this.isCurrentlyTraining = false;
		loadFromFile();
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
	 * @param sample
	 * @return probabilities of the labels
	 */
	protected int predict(double[] sample) {
		return model.predict(sample);
	}

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
	 * @param k
	 *            for k-fold cross-validation. (Typical values are 3 or 10.)
	 * @param groundTruthData
	 *            {@link TrainingData} used for training and evaluation in k-fold
	 *            cross-validation.
	 * @return map of evaluation results.
	 */
	public abstract Map<String, Double> evaluateClassifier(int k, TrainingData groundTruthData);

	/**
	 * @param groundTruthData
	 *            {@link TrainingData} used for evaluation. The classifier needs to
	 *            be already trained on different data!
	 * @return map of evaluation results (e.g. of cross-project validation).
	 */
	public abstract Map<String, Double> evaluateClassifier(TrainingData groundTruthData);

	/**
	 * Saves model to a file, so that it can be loaded at a later time.
	 *
	 * @param filePathAndName
	 *            absolute path to the classifier file that should be saved.
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
	 * Saves model to a file, so that it can be loaded at a later time.
	 */
	public File saveToFile() {
		return saveToFile(TextClassifier.CLASSIFIER_DIRECTORY + getName());
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
		return loadFromFile(TextClassifier.CLASSIFIER_DIRECTORY + getName());
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

	/**
	 * @param values
	 *            array of prediction results.
	 * @return mode, i.e. the most frequent value in the array.
	 */
	public static int mode(int[] values) {
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		for (int value : values) {
			Integer count = map.get(value);
			map.put(value, count != null ? count + 1 : 1);
		}
		int mode = Collections.max(map.entrySet(), new Comparator<Map.Entry<Integer, Integer>>() {
			@Override
			public int compare(Entry<Integer, Integer> o1, Entry<Integer, Integer> o2) {
				return o1.getValue().compareTo(o2.getValue());
			}
		}).getKey();
		return mode;
	}
}
