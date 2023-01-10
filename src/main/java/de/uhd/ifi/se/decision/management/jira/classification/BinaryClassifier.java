package de.uhd.ifi.se.decision.management.jira.classification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.apache.commons.io.FilenameUtils;

import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.PreprocessedData;
import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.Preprocessor;
import de.uhd.ifi.se.decision.management.jira.model.PartOfJiraIssueText;
import smile.classification.Classifier;
import smile.classification.LogisticRegression;
import smile.classification.NaiveBayes;
import smile.classification.SVM;
import smile.math.MathEx;
import smile.math.kernel.GaussianKernel;
import smile.stat.distribution.Distribution;
import smile.stat.distribution.GaussianMixture;
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

	/**
	 * Constructs a new binary classifier instance. Reads the classifier from file
	 * if it was already trained and saved to file system.
	 * 
	 * @param namePrefix
	 *            to identify the ground truth data that the classifier was trained
	 *            on, e.g. "defaultTrainingData" or a project key.
	 */
	public BinaryClassifier(String namePrefix) {
		super(2, namePrefix);
	}

	@Override
	public String getName() {
		return "binaryClassifier.model";
	}

	@Override
	public void train(GroundTruthData trainingData, ClassifierType classifierType) {
		isCurrentlyTraining = true;
		namePrefix = FilenameUtils.getBaseName(trainingData.getFileName());
		long start = System.nanoTime();
		PreprocessedData preprocessedData = new PreprocessedData(trainingData, false);
		model = train(preprocessedData.preprocessedSentences, preprocessedData.getIsRelevantLabels(), classifierType);
		fitTime = (System.nanoTime() - start) / 1E6;
		isCurrentlyTraining = false;
		saveToFile();
	}

	@Override
	public Classifier<double[]> train(double[][] trainingSamples, int[] trainingLabels, ClassifierType classifierType) {
		int p = trainingSamples[0].length; // vector length 150 per 3-gram
		int k = MathEx.max(trainingLabels) + 1; // number of classes is 2, labels are -1 and +1

		switch (classifierType) {
		case SVM:
			return SVM.fit(trainingSamples, trainingLabels, new GaussianKernel(1.0), 2, 0.5);
		case NB:
			int n = trainingSamples.length; // number of 3-grams for training
			double[] priori = new double[k];
			Distribution[][] condprob = new Distribution[k][p];
			for (int i = 0; i < k; i++) {
				priori[i] = 1.0 / k;
				final int c = i == 0 ? -1 : i; // labels are -1 and +1
				for (int j = 0; j < p; j++) {
					final int f = j;
					double[] xi = IntStream.range(0, n).filter(l -> trainingLabels[l] == c)
							.mapToDouble(l -> trainingSamples[l][f]).toArray();
					condprob[i][j] = GaussianMixture.fit(3, xi);
				}
			}
			return new NaiveBayes(priori, condprob);
		default:
			return LogisticRegression.binomial(trainingSamples, trainingLabels);
		}
	}

	@Override
	public Map<String, ClassificationMetrics> evaluateUsingKFoldCrossValidation(int k, GroundTruthData groundTruthData,
			ClassifierType classifierType) {
		Map<GroundTruthData, GroundTruthData> splitData = groundTruthData.splitForBinaryKFoldCrossValidation(k);
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
			validations.add(new ClassificationValidation<Classifier<double[]>>(model, fitTime, scoreTime, truthForFold,
					predictionForFold));
		}
		Map<String, ClassificationMetrics> binaryEvaluationResult = Map.of("Binary " + model.getClass().getName(),
				new ClassificationValidations<Classifier<double[]>>(validations).avg);
		model = entireModel;
		return binaryEvaluationResult;
	}

	@Override
	public Map<String, ClassificationMetrics> evaluateTrainedClassifier(GroundTruthData groundTruthData) {
		long start = System.nanoTime();
		String[] sentences = groundTruthData.getAllSentences();
		int[] truth = groundTruthData.getRelevanceLabelsForAllSentences();

		int[] prediction = new int[sentences.length];
		for (int i = 0; i < sentences.length; i++) {
			prediction[i] = predict(sentences[i]) ? 1 : 0;
		}
		double scoreTime = (System.nanoTime() - start) / 1E6;
		ClassificationValidation<Classifier<double[]>> validation = new ClassificationValidation<Classifier<double[]>>(
				model, fitTime, scoreTime, truth, prediction);
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
