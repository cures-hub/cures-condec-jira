package de.uhd.ifi.se.decision.management.jira.classification;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import smile.classification.LogisticRegression;
import smile.classification.OnlineClassifier;
import smile.math.kernel.GaussianKernel;
import smile.math.kernel.MercerKernel;

public abstract class AbstractClassifier {

	public static final String DEFAULT_PATH = DecisionKnowledgeClassifier.DEFAULT_DIR;

	protected OnlineClassifier<double[]> model;
	private int epochs;
	private boolean modelIsTrained;
	private int numClasses;
	private boolean currentlyTraining;

	public AbstractClassifier(int numClasses) {
		this(0.5, 3, numClasses);
	}

	public AbstractClassifier(Double c, Integer epochs, int numClasses) {
		this(c, new GaussianKernel(1.0), epochs, numClasses);
	}

	public AbstractClassifier(Double c, MercerKernel<double[]> kernel, Integer epochs, int numClasses) {
		this.epochs = epochs;
		this.modelIsTrained = false;
		this.numClasses = numClasses;
		this.currentlyTraining = false;
	}

	/**
	 * Trains the model using supervised training data, features and labels.
	 *
	 * @param features
	 * @param labels
	 */
	public void train(double[][] features, int[] labels) {
		this.model = LogisticRegression.fit(features, labels);
	}

	/**
	 * Trains the model using supervised training data, features and labels.
	 *
	 * @param feature
	 * @param label
	 */
	public void train(double[] feature, int label) {
		this.modelIsTrained = true;
		this.model.update(feature, label);
	}

	/**
	 * @param feature
	 * @return probabilities of the labels
	 */
	public double[] predictProbabilities(double[] feature) {
		double[] probabilities = new double[this.numClasses];
		int prediction = this.model.predict(feature);
		probabilities[0] = prediction;
		return probabilities;
	}

	/**
	 * Saves model to a file. So that it can be loaded at a later time.
	 *
	 * @param filePathAndName
	 * @throws Exception
	 */
	public void saveToFile(String filePathAndName) {
		try {
			FileOutputStream fileOut = new FileOutputStream(filePathAndName);
			ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
			objectOut.writeObject(model);
			objectOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Saves model to a file. So that it can be loaded at a later time.
	 *
	 * @throws Exception
	 */
	public abstract void saveToFile() throws Exception;

	/**
	 * Loads pre-trained model from file.
	 *
	 * @param filePathAndName
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public boolean loadFromFile(String filePathAndName) {
		OnlineClassifier<double[]> oldModel = this.model;
		try {
			FileInputStream fileIn = new FileInputStream(filePathAndName);
			ObjectInputStream objectIn = new ObjectInputStream(fileIn);
			this.model = (OnlineClassifier<double[]>) objectIn.readObject();
			this.modelIsTrained = true;
			objectIn.close();
		} catch (Exception e) {
			this.model = oldModel;
			return false;
		}
		return true;
	}

	/**
	 * Loads pre-trained model from file.
	 *
	 * @throws Exception
	 */
	public abstract boolean loadFromFile();

	public boolean isModelTrained() {
		return this.modelIsTrained;
	}

	public Integer getNumClasses() {
		return numClasses;
	}

	public Boolean isCurrentlyTraining() {
		return this.currentlyTraining;
	}
}
