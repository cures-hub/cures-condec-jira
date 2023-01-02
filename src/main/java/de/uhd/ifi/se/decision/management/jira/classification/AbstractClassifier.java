package de.uhd.ifi.se.decision.management.jira.classification;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.Preprocessor;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import smile.classification.Classifier;
import smile.validation.ClassificationMetrics;

/**
 * The {@link TextClassifier} comprises a binary and a fine-grained classifier.
 * This is the abstract superclass for the {@link BinaryClassifier} and
 * {@link FineGrainedClassifier}.
 */
public abstract class AbstractClassifier {

	protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractClassifier.class);

	protected Classifier<double[]> model;
	protected int numClasses;
	protected boolean isCurrentlyTraining;
	protected String namePrefix;
	protected double fitTime;

	/**
	 * Constructs a new classifier instance. Reads the classifier from file if it
	 * was already trained and saved to file system.
	 * 
	 * @param numClasses
	 *            2 for the binary classifier and > 2 for the fine grained
	 *            classifier.
	 * @param namePrefix
	 *            to identify the ground truth data that the classifier was trained
	 *            on, e.g. "defaultTrainingData" or a project key.
	 */
	public AbstractClassifier(int numClasses, String namePrefix) {
		this.numClasses = numClasses;
		this.isCurrentlyTraining = false;
		this.namePrefix = namePrefix;
		Classifier<double[]> modelFromFile = loadFromFile();
		if (modelFromFile != null) {
			this.model = modelFromFile;
		}
	}

	/**
	 * @return file name of the classifier, e.g. binaryClassifier.model
	 */
	public abstract String getName();

	/**
	 * @return full name of the trained classifier for ground truth data, e.g.
	 *         "CONDEC-binaryClassifier.model".
	 */
	public String getNameWithPrefix() {
		return namePrefix + "-" + getName();
	}

	/**
	 * Trains the model using supervised training data. Preprocesses the training
	 * data to extract features and labels for training. Saves the model to file
	 * afterwards.
	 *
	 * @param trainingData
	 *            {@link GroundTruthData} that the classifier should be trained on.
	 *            Is read from csv file (see {@link #readDataFrameFromCSVFile(File)}
	 *            and can be created from the current {@link KnowledgeGraph}).
	 * @param classifierType
	 *            machine learning algorithm, e.g. support vector machine or
	 *            logistic regression (see {@link ClassifierType}).
	 */
	public abstract void train(GroundTruthData trainingData, ClassifierType classifierType);

	/**
	 * @param trainingSamples
	 *            features for training (e.g. numerical representation of n-grams,
	 *            see {@link Preprocessor}).
	 * @param trainingLabels
	 *            labels for training, either for the binary relevance or the
	 *            knowledge type.
	 * @param classifierType
	 *            machine learning algorithm, e.g. support vector machine or
	 *            logistic regression (see {@link ClassifierType}).
	 * @return {@link Classifier} instance.
	 */
	public abstract Classifier<double[]> train(double[][] trainingSamples, int[] trainingLabels,
			ClassifierType classifierType);

	/**
	 * @param sample
	 *            feature that should be classified (e.g. numerical representation
	 *            of n-gram, see {@link Preprocessor}).
	 * @return classification label (e.g. -1 for irrelevant or 1 for relevant during
	 *         binary classification).
	 */
	protected int predict(double[] sample) {
		return model.predict(sample);
	}

	/**
	 * Updates the classifier using supervised training data. Used for online
	 * training. That means that manually approved parts of text are directly used
	 * for training.
	 *
	 * @param trainingSamples
	 *            features for training (e.g. numerical representation of n-grams,
	 *            see {@link Preprocessor}).
	 * @param trainingLabel
	 *            label for training, either for the binary relevance or the
	 *            knowledge type.
	 */
	public void update(double[][] trainingSample, int trainingLabel) {
		isCurrentlyTraining = true;
		if (model.online()) {
			int[] trainingLabelArray = new int[trainingSample.length];
			Arrays.fill(trainingLabelArray, trainingLabel);
			model.update(trainingSample, trainingLabelArray);
		}
		isCurrentlyTraining = false;
	}

	/**
	 * @param k
	 *            for k-fold cross-validation. (Typical values are 3 or 10.)
	 * @param groundTruthData
	 *            {@link GroundTruthData} used for training and evaluation in k-fold
	 *            cross-validation.
	 * @return map of evaluation results (of k-fold cross validation).
	 */
	public abstract Map<String, ClassificationMetrics> evaluateUsingKFoldCrossValidation(int k,
			GroundTruthData groundTruthData, ClassifierType classifierType);

	/**
	 * @param groundTruthData
	 *            {@link GroundTruthData} used for evaluation. The classifier needs
	 *            to be already trained on different data!
	 * @return map of evaluation results (e.g. of cross-project validation).
	 */
	public abstract Map<String, ClassificationMetrics> evaluateTrainedClassifier(GroundTruthData groundTruthData);

	/**
	 * Saves model to a file, so that it can be loaded at a later time.
	 *
	 * @param filePathAndName
	 *            absolute path to the classifier file that should be saved.
	 * @return {@link File} object that was created.
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
	 * 
	 * @return {@link File} object that was created.
	 */
	public File saveToFile() {
		return saveToFile(TextClassifier.CLASSIFIER_DIRECTORY + getNameWithPrefix());
	}

	/**
	 * Loads a pre-trained classifier from file.
	 *
	 * @param filePathAndName
	 *            absolute path to the classifier file that should be loaded.
	 * @return {@link Classifier} instance.
	 */
	@SuppressWarnings("unchecked")
	public static Classifier<double[]> loadFromFile(String filePathAndName) {
		Classifier<double[]> model = null;
		try {
			FileInputStream fileIn = new FileInputStream(filePathAndName);
			ObjectInputStream objectIn = new ObjectInputStream(fileIn);
			model = (Classifier<double[]>) objectIn.readObject();
			objectIn.close();
		} catch (Exception e) {
			LOGGER.error("Could not load a classifier from file: " + filePathAndName + ". " + e.getMessage());
		}
		return model;
	}

	/**
	 * Loads pre-trained classifier from file.
	 * 
	 * @return {@link Classifier} instance.
	 */
	public Classifier<double[]> loadFromFile() {
		return loadFromFile(TextClassifier.CLASSIFIER_DIRECTORY + getNameWithPrefix());
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
		if (values.length == 0) {
			return 0;
		}
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
