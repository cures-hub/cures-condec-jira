package de.uhd.ifi.se.decision.management.jira.classification;

import java.io.File;
import java.util.List;

import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.PreprocessedData;
import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.Preprocessor;
import de.uhd.ifi.se.decision.management.jira.model.PartOfJiraIssueText;
import smile.classification.SVM;
import smile.math.kernel.GaussianKernel;

/**
 * Binary classifier that predicts whether a sentence (i.e.
 * {@link PartOfJiraIssueText}) contains relevant decision knowledge or is
 * irrelevant.
 * 
 * Extends the {@link AbstractClassifier} and is used in the
 * {@link TextClassifier}.
 */
public class BinaryClassifier extends AbstractClassifier {

	public BinaryClassifier() {
		super(2);
	}

	@Override
	public String getName() {
		return "binaryClassifier.model";
	}

	/**
	 * Trains the binary classifier.
	 *
	 * @param trainingData
	 *            {@link TrainingData} read from csv file (see
	 *            {@link #readDataFrameFromCSVFile(File)} or created from the
	 *            current {@link KnowledgeGraph).
	 */
	@Override
	public void train(TrainingData trainingData) {
		isCurrentlyTraining = true;
		PreprocessedData preprocessedData = new PreprocessedData(trainingData, false);
		train(preprocessedData.preprocessedSentences, preprocessedData.getIsRelevantLabels());
		isCurrentlyTraining = false;
		saveToFile();
	}

	/**
	 * Trains the model using supervised training data, features and labels.
	 *
	 * @param trainingSamples
	 * @param trainingLabels
	 */
	private void train(double[][] trainingSamples, int[] trainingLabels) {
		model = SVM.fit(trainingSamples, trainingLabels, new GaussianKernel(1.0), 2, 0.5);
		// model = LogisticRegression.binomial(trainingSamples, trainingLabels);
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
	public boolean predict(String sentence) {
		double[][] features = Preprocessor.getInstance().preprocess(sentence);
		// Make prediction for each nGram and determine most frequent prediction
		// result.
		int predictionResults[] = new int[features.length];
		for (int i = 0; i < features.length; i++) {
			predictionResults[i] = predict(features[i]);
		}
		int predictionResult = mode(predictionResults);
		return predictionResult > 0;
	}
}
