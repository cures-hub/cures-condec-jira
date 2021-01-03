package de.uhd.ifi.se.decision.management.jira.classification;

import java.util.List;

import com.atlassian.gzipfilter.org.apache.commons.lang.ArrayUtils;

import de.uhd.ifi.se.decision.management.jira.classification.implementation.GaussianKernelDouble;
import smile.classification.SVM;
import smile.math.kernel.MercerKernel;
import weka.core.SerializationHelper;

public abstract class AbstractClassifier {

	public static final String DEFAULT_PATH = DecisionKnowledgeClassifier.DEFAULT_DIR;

	protected SVM<Double[]> model;
	private Integer epochs;
	private boolean modelIsTrained;
	private Integer numClasses;
	private Boolean currentlyTraining;

	public AbstractClassifier(Integer numClasses) {
		this(0.5, 3, numClasses);
	}

	public AbstractClassifier(Double c, Integer epochs, Integer numClasses) {
		this(c, new GaussianKernelDouble<Double>(), epochs, numClasses);
	}

	public AbstractClassifier(Double c, MercerKernel<Double[]> kernel, Integer epochs, Integer numClasses) {
		Double[][] instances = new Double[1][1];
		double[] weight = new double[1];
		this.model = new SVM<Double[]>(kernel, instances, weight, c);
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
	public void train(double[][] features, Integer[] labels) throws AlreadyInTrainingException {
		double c = 1;
		double tol = 1;
		if (!this.currentlyTraining) {
			this.currentlyTraining = true;
			for (int i = 0; i < this.epochs; i++) {
				this.model.fit(features, ArrayUtils.toPrimitive(labels), c, tol);
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
	public void train(List<List<Double>> features, List<Integer> labels) {
		Double[][] featuresArray = new Double[features.size()][features.get(0).size()];
		for (int i = 0; i < features.size(); i++) {
			featuresArray[i] = features.get(i).toArray(Double[]::new);
		}
		// this.train(featuresArray, labels.toArray(Integer[]::new));
	}

	/**
	 * Trains the model using supervised training data, features and labels.
	 *
	 * @param features
	 * @param label
	 */
	public void train(Double[] features, Integer label) {
		this.modelIsTrained = true;

		// this.model.learn(features, label);

		this.modelIsTrained = true;
	}

	/**
	 * @param feature
	 * @return probabilities of the labels
	 */
	public double[] predictProbabilities(Double[] feature) throws InstantiationError {
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
		SVM<Double[]> oldModel = this.model;
		try {
			this.model = (SVM<Double[]>) SerializationHelper.read(filePathAndName);
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
