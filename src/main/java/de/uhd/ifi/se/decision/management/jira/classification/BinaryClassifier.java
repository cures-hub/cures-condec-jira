package de.uhd.ifi.se.decision.management.jira.classification;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.PreprocessedData;
import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.Preprocessor;
import de.uhd.ifi.se.decision.management.jira.model.PartOfJiraIssueText;
import smile.classification.Classifier;
import smile.classification.LogisticRegression;
import smile.validation.ClassificationValidations;
import smile.validation.CrossValidation;
import smile.validation.metric.Accuracy;
import smile.validation.metric.Error;
import smile.validation.metric.FScore;
import smile.validation.metric.Precision;
import smile.validation.metric.Sensitivity;

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
		model = train(preprocessedData.preprocessedSentences, preprocessedData.getIsRelevantLabels());
		isCurrentlyTraining = false;
		saveToFile();
	}

	/**
	 * Trains the model using supervised training data, features and labels.
	 *
	 * @param trainingSamples
	 * @param trainingLabels
	 * @return
	 */
	public Classifier<double[]> train(double[][] trainingSamples, int[] trainingLabels) {
		// return SVM.fit(trainingSamples, trainingLabels, new GaussianKernel(1.0), 2,
		// 0.5);
		return LogisticRegression.binomial(trainingSamples, trainingLabels);
	}

	@Override
	public Map<String, Double> evaluateClassifier(int k, TrainingData groundTruthData) {
		Map<String, Double> resultsMap = new LinkedHashMap<>();
		PreprocessedData preprocessedData = new PreprocessedData(groundTruthData, false);
		ClassificationValidations<Classifier<double[]>> validations = CrossValidation.classification(k,
				preprocessedData.preprocessedSentences, preprocessedData.updatedLabels, this::train);

		resultsMap.put("Binary Precision", validations.avg.precision);
		resultsMap.put("Binary Recall", validations.avg.sensitivity);
		resultsMap.put("Binary F1", validations.avg.f1);
		resultsMap.put("Binary Accuracy", validations.avg.accuracy);
		resultsMap.put("Binary Number of Errors", (double) validations.avg.error);
		return resultsMap;
	}

	@Override
	public Map<String, Double> evaluateClassifier(TrainingData groundTruthData) {
		Map<String, Double> resultsMap = new LinkedHashMap<>();
		String[] sentences = groundTruthData.getAllSentences();
		int[] truth = groundTruthData.getRelevanceLabelsForAllSentences();

		int[] prediction = new int[sentences.length];
		for (int i = 0; i < sentences.length; i++) {
			prediction[i] = predict(sentences[i]) ? 1 : 0;
		}

		resultsMap.put("Binary Precision", Precision.of(truth, prediction));
		resultsMap.put("Binary Recall", Sensitivity.of(truth, prediction));
		resultsMap.put("Binary F1", FScore.of(1.0, truth, prediction));
		resultsMap.put("Binary Accuracy", Accuracy.of(truth, prediction));
		resultsMap.put("Binary Number of Errors", (double) Error.of(truth, prediction));
		return resultsMap;
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
