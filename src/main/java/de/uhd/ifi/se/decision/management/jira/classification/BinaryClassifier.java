package de.uhd.ifi.se.decision.management.jira.classification;

import java.util.List;

import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.PreprocessedData;
import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.Preprocessor;
import smile.classification.SVM;
import smile.math.kernel.GaussianKernel;

public class BinaryClassifier extends AbstractClassifier {

	public BinaryClassifier() {
		super(2);
		loadFromFile();
	}

	@Override
	public String getName() {
		return "binaryClassifier.model";
	}

	/**
	 * Trains the model.
	 *
	 * @param trainingData
	 */
	public void train(TrainingData trainingData) {
		PreprocessedData preprocessedData = new PreprocessedData(trainingData, false);
		train(preprocessedData.preprocessedSentences, preprocessedData.getIsRelevantLabels());
		// saveToFile();
	}

	/**
	 * Trains the model using supervised training data, features and labels.
	 *
	 * @param trainingSamples
	 * @param trainingLabels
	 */
	@Override
	public void train(double[][] trainingSamples, int[] trainingLabels) {
		this.model = SVM.fit(trainingSamples, trainingLabels, new GaussianKernel(8.0), 10,
				1E-3);
		// model = LogisticRegression.fit(trainingSamples, trainingLabels);
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
	 * @return boolean value that indicates whether the sentence is relevant (true)
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
		int predictionResult = DecisionKnowledgeClassifier.mode(predictionResults);
		return predictionResult > 0;
	}
}
