package de.uhd.ifi.se.decision.management.jira.classification;

import java.util.List;

import smile.classification.SVM;
import smile.math.kernel.GaussianKernel;
import smile.math.kernel.MercerKernel;
import weka.core.SerializationHelper;

public abstract class AbstractClassifier {

	public static final String DEFAULT_PATH = DecisionKnowledgeClassifier.DEFAULT_DIR;

	protected SVM<double[]> model;
	private Integer epochs;
	private boolean modelIsTrained;
	private int numClasses;
	private Boolean currentlyTraining;
	private MercerKernel<double[]> kernel;

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
		this.kernel = kernel;
	}

	/**
	 * Trains the model using supervised training data, features and labels.
	 *
	 * @param features
	 * @param labels
	 */
	public void train(double[][] features, int[] labels) throws AlreadyInTrainingException {
		Double[][] instances = new Double[1][1];
		double[] weight = new double[1];
		double b = 1;
		this.model = new SVM<double[]>(kernel, features, weight, b);
		double c = 1;
		double tol = 1;
		if (!this.currentlyTraining) {
			this.currentlyTraining = true;
			for (int i = 0; i < this.epochs; i++) {
				this.model.fit(features, labels, c, tol);
			}
			// this.model.finish();

			// this.model.trainPlattScaling(features, ArrayUtils.toPrimitive(labels));

			this.currentlyTraining = false;
			this.modelIsTrained = true;
		} else {
			throw new AlreadyInTrainingException(this.toString() + " is already in training!");
		}

	}

	/**
	 * Trains the model using supervised training data, features and labels.
	 *
	 * @param features
	 * @param labels
	 */
	public void train(List<double[]> features, List<Integer> labels) {
		double[][] featuresArray = new double[features.size()][features.get(0).length];
		for (int i = 0; i < features.size(); i++) {
			featuresArray[i] = features.get(i);
		}
		// this.train(featuresArray, labels.toArray(Integer[]::new));
	}

	/**
	 * Trains the model using supervised training data, features and labels.
	 *
	 * @param features
	 * @param label
	 */
	public void train(double[] features, Integer label) {
		this.modelIsTrained = true;

		// this.model.learn(features, label);

		this.modelIsTrained = true;
	}

	/**
	 * @param feature
	 * @return probabilities of the labels
	 */
	public double[] predictProbabilities(double[] feature) throws InstantiationError {
		if (this.modelIsTrained) {
			double[] probabilities = new double[this.numClasses];
			// this.model.predict(feature, probabilities);
			this.model.predict(feature);
			return probabilities;
		} else {
			throw new InstantiationError("Classifier has not been trained!");
		}

	}

	/**
	 * Saves model to a file. So that it can be loaded at a later time.
	 *
	 * @param filePathAndName
	 * @throws Exception
	 */
	public void saveToFile(String filePathAndName) throws Exception {
		SerializationHelper.write(filePathAndName, this.model);
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
		SVM<double[]> oldModel = this.model;
		try {
			this.model = (SVM<double[]>) SerializationHelper.read(filePathAndName);
			this.modelIsTrained = true;
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
