package de.uhd.ifi.se.decision.management.jira.classification;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.PreprocessedData;
import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.Preprocessor;
import de.uhd.ifi.se.decision.management.jira.model.PartOfJiraIssueText;
import smile.classification.Classifier;
import smile.classification.LogisticRegression;
import smile.classification.SVM;
import smile.math.kernel.GaussianKernel;
import smile.validation.ClassificationMetrics;
import smile.validation.ClassificationValidation;
import smile.validation.ClassificationValidations;

/**
 * Binary classifier that predicts whether a sentence (i.e.
 * {@link PartOfJiraIssueText}) contains relevant decision knowledge or is
 * irrelevant.
 * 
 * Extends the {@link AbstractClassifier} and is used in the
 * {@link TextClassifier}.
 */
public class BinaryClassifier extends AbstractClassifier {

	public BinaryClassifier(String namePrefix) {
		super(2, namePrefix);
	}

	@Override
	public String getName() {
		return "binaryClassifier.model";
	}

	/**
	 * Trains the binary classifier.
	 *
	 * @param trainingData
	 *            {@link GroundTruthData} read from csv file (see
	 *            {@link #readDataFrameFromCSVFile(File)} or created from the
	 *            current {@link KnowledgeGraph).
	 */
	@Override
	public void train(GroundTruthData trainingData, ClassifierType classifierType) {
		isCurrentlyTraining = true;
		long start = System.nanoTime();
		PreprocessedData preprocessedData = new PreprocessedData(trainingData, false);
		model = train(preprocessedData.preprocessedSentences, preprocessedData.getIsRelevantLabels(), classifierType);
		fitTime = (System.nanoTime() - start) / 1E6;
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
	public Classifier<double[]> train(double[][] trainingSamples, int[] trainingLabels, ClassifierType classifierType) {
		switch (classifierType) {
		case SVM:
			return SVM.fit(trainingSamples, trainingLabels, new GaussianKernel(1.0), 2, 0.5);
		default:
			return LogisticRegression.binomial(trainingSamples, trainingLabels);
		}
	}

	@Override
	public Map<String, ClassificationMetrics> evaluate(int k, GroundTruthData groundTruthData,
			ClassifierType classifierType) {
		Map<GroundTruthData, GroundTruthData> splitData = GroundTruthData.splitForKFoldCrossValidation(k,
				groundTruthData.getKnowledgeElements());
		Classifier<double[]> entireModel = model;

		List<ClassificationValidation<Classifier<double[]>>> validations = new ArrayList<>();
		for (Map.Entry<GroundTruthData, GroundTruthData> entry : splitData.entrySet()) {
			long start = System.nanoTime();
			train(entry.getKey(), classifierType);
			double fitTime = (System.nanoTime() - start) / 1E6;
			start = System.nanoTime();
			String[] sentences = entry.getValue().getAllSentences();
			int[] truthForFold = entry.getValue().getRelevanceLabelsForAllSentences();
			int[] predictionForFold = new int[sentences.length];
			for (int i = 0; i < sentences.length; i++) {
				predictionForFold[i] = predict(sentences[i]) ? 1 : 0;
			}
			double scoreTime = (System.nanoTime() - start) / 1E6;
			validations.add(new ClassificationValidation<Classifier<double[]>>(model, truthForFold, predictionForFold,
					fitTime, scoreTime));
		}
		Map<String, ClassificationMetrics> binaryEvaluationResult = Map.of("Binary " + model.getClass().getName(),
				new ClassificationValidations<Classifier<double[]>>(validations).avg);
		model = entireModel;
		return binaryEvaluationResult;
	}

	@Override
	public Map<String, ClassificationMetrics> evaluate(GroundTruthData groundTruthData) {
		long start = System.nanoTime();
		String[] sentences = groundTruthData.getAllSentences();
		int[] truth = groundTruthData.getRelevanceLabelsForAllSentences();

		int[] prediction = new int[sentences.length];
		for (int i = 0; i < sentences.length; i++) {
			prediction[i] = predict(sentences[i]) ? 1 : 0;
		}
		double scoreTime = (System.nanoTime() - start) / 1E6;
		ClassificationValidation<Classifier<double[]>> validation = new ClassificationValidation<Classifier<double[]>>(
				model, truth, prediction, fitTime, scoreTime);
		return Map.of("Binary " + model.getClass().getName(), validation.metrics);
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
