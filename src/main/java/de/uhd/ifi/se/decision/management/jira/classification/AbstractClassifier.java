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

	protected OnlineClassifier<double[]> model;
	private int numClasses;
	private boolean currentlyTraining;

	public AbstractClassifier(int numClasses) {
		this(0.5, 3, numClasses);
	}

	public AbstractClassifier(Double c, Integer epochs, int numClasses) {
		this(c, new GaussianKernel(1.0), epochs, numClasses);
	}

	public AbstractClassifier(Double c, MercerKernel<double[]> kernel, Integer epochs, int numClasses) {
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
		model = LogisticRegression.fit(features, labels);
	}

	/**
	 * Trains the model using supervised training data, features and labels.
	 *
	 * @param feature
	 * @param label
	 */
	public void update(double[] feature, int label) {
		model.update(feature, label);
	}

	/**
	 * @param feature
	 * @return probabilities of the labels
	 */
	public int predict(double[] feature) {
		return model.predict(feature);
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
	 */
	@SuppressWarnings("unchecked")
	public boolean loadFromFile(String filePathAndName) {
		OnlineClassifier<double[]> oldModel = model;
		try {
			FileInputStream fileIn = new FileInputStream(filePathAndName);
			ObjectInputStream objectIn = new ObjectInputStream(fileIn);
			model = (OnlineClassifier<double[]>) objectIn.readObject();
			objectIn.close();
		} catch (Exception e) {
			model = oldModel;
			return false;
		}
		return true;
	}

	/**
	 * Loads pre-trained model from file.
	 */
	public abstract boolean loadFromFile();

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
