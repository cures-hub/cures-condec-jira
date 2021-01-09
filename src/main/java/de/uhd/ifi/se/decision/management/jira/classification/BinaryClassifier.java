package de.uhd.ifi.se.decision.management.jira.classification;

import java.io.File;
import java.util.List;

import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.PreprocessedData;
import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.Preprocessor;
import smile.classification.LogisticRegression;

public class BinaryClassifier extends AbstractClassifier {

	public static final String DEFAULT_MODEL_NAME = "binaryClassifier.model";

	public BinaryClassifier() {
		super(2);
	}

	public boolean isSentenceRelevant(String sentenceToBeClassified) {
		// TODO Implement binary classification here
		return true;
	}

	@Override
	public boolean loadFromFile() {
		return super.loadFromFile(DecisionKnowledgeClassifier.CLASSIFIER_DIRECTORY + BinaryClassifier.DEFAULT_MODEL_NAME);
	}

	@Override
	public File saveToFile() {
		return super.saveToFile(DecisionKnowledgeClassifier.CLASSIFIER_DIRECTORY + BinaryClassifier.DEFAULT_MODEL_NAME);
	}

	/**
	 * Trains the model.
	 *
	 * @param trainingData
	 */
	public void train(TrainingData trainingData) {
		PreprocessedData preprocessedData = new PreprocessedData(trainingData, false);
		train(preprocessedData.preprocessedSentences, preprocessedData.updatedLabels);
	}

	/**
	 * Trains the model using supervised training data, features and labels.
	 *
	 * @param trainingSamples
	 * @param trainingLabels
	 */
	@Override
	public void train(double[][] trainingSamples, int[] trainingLabels) {
		// if (numClasses <= 2) {
		// this.model = (OnlineClassifier<double[]>) SVM.fit(trainingSamples,
		// trainingLabels, new GaussianKernel(8.0),
		// 500, 1E-3);
		// } else {
		this.model = LogisticRegression.fit(trainingSamples, trainingLabels);
		// }
	}

	public static boolean isRelevant(double[] probabilities) {
		return probabilities[1] > 0.5;
	}

	/**
	 * @param sentences
	 *            list of sentences that should be classified as relevant or
	 *            irrelevant wrt. decision knowledge.
	 * @return boolean array in the same order as the input strings with
	 *         classification results. Each value indicates whether a string is
	 *         relevant (true) or not (false).
	 */
	public boolean[] predict(List<String> sentences) {
		boolean[] binaryPredictionResults = new boolean[sentences.size()];
		for (int i = 0; i < sentences.size(); i++) {
			binaryPredictionResults[i] = predict(sentences.get(i));
		}
		return binaryPredictionResults;
	}

	/**
	 * @param sentence
	 *            sentence that should be classified as relevant or irrelevant wrt.
	 *            decision knowledge.
	 * @return boolea value that indicates whether the sentence is relevant (true)
	 *         or not (false).
	 */
	private boolean predict(String sentence) {
		double[][] features = Preprocessor.getInstance().preprocess(sentence);
		// Make predictions for each nGram; then determine maximum probability of all
		// added together.
		int predictionResults[] = new int[features.length];
		for (int i = 0; i < features.length; i++) {
			predictionResults[i] = predict(features[i]);
		}
		double predictionResult = DecisionKnowledgeClassifier.mode(predictionResults);
		return predictionResult >= 0.5;
	}
}
