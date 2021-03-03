package de.uhd.ifi.se.decision.management.jira.classification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.atlassian.gzipfilter.org.apache.commons.lang.ArrayUtils;
import com.atlassian.jira.template.TemplateSource.File;

import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.PreprocessedData;
import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.Preprocessor;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.PartOfJiraIssueText;
import smile.classification.Classifier;
import smile.classification.LogisticRegression;
import smile.validation.ClassificationMetrics;
import smile.validation.ClassificationValidation;

/**
 * Fine grained classifier that predicts the {@link KnowledgeType} of a sentence
 * (i.e. {@link PartOfJiraIssueText}).
 * 
 * Extends the {@link AbstractClassifier} and is used in the
 * {@link TextClassifier}.
 */
public class FineGrainedClassifier extends AbstractClassifier {

	public FineGrainedClassifier(int numClasses, String namePrefix) {
		super(numClasses, namePrefix);
	}

	@Override
	public String getName() {
		return "fineGrainedClassifier.model";
	}

	/**
	 * Trains the fine grained classifier.
	 *
	 * @param trainingData
	 *            {@link GroundTruthData} read from csv file (see
	 *            {@link #readDataFrameFromCSVFile(File)} or created from the
	 *            current {@link KnowledgeGraph).
	 */
	@Override
	public void train(GroundTruthData trainingData) {
		isCurrentlyTraining = true;
		long start = System.nanoTime();
		PreprocessedData preprocessedData = new PreprocessedData(trainingData, true);
		model = train(preprocessedData.preprocessedSentences, preprocessedData.updatedLabels);
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
	public Classifier<double[]> train(double[][] trainingSamples, int[] trainingLabels) {
		// return OneVersusRest.fit(trainingSamples, trainingLabels,
		// (x, y) -> SVM.fit(x, y, new GaussianKernel(1.0), 5, 0.5));
		return LogisticRegression.multinomial(trainingSamples, trainingLabels);
	}

	@Override
	public Map<String, ClassificationMetrics> evaluate(int k, GroundTruthData groundTruthData) {
		Map<GroundTruthData, GroundTruthData> splitData = GroundTruthData.splitForKFoldCrossValidation(k,
				groundTruthData.getDecisionKnowledgeElements());
		Classifier<double[]> oldModel = model;

		int[] truth = new int[0];
		int[] prediction = new int[0];
		double fitTime = 0;
		double scoreTime = 0;
		for (Map.Entry<GroundTruthData, GroundTruthData> entry : splitData.entrySet()) {
			long start = System.nanoTime();
			train(entry.getKey());
			fitTime += (System.nanoTime() - start) / 1E6;
			start = System.nanoTime();
			String[] sentences = entry.getValue().getRelevantSentences();
			int[] truthForFold = entry.getValue().getKnowledgeTypeLabelsForRelevantSentences();
			int[] predictionForFold = new int[sentences.length];
			for (int i = 0; i < sentences.length; i++) {
				predictionForFold[i] = mapKnowledgeTypeToIndex(predict(sentences[i]));
			}
			scoreTime += (System.nanoTime() - start) / 1E6;
			truth = PreprocessedData.concatenate(truth, truthForFold);
			prediction = PreprocessedData.concatenate(prediction, predictionForFold);
		}
		model = oldModel;
		return calculateEvaluationMetrics(truth, prediction, fitTime, scoreTime);
	}

	@Override
	public Map<String, ClassificationMetrics> evaluateClassifier(GroundTruthData groundTruthData) {
		long start = System.nanoTime();
		String[] sentences = groundTruthData.getRelevantSentences();
		int[] truth = groundTruthData.getKnowledgeTypeLabelsForRelevantSentences();

		int[] prediction = new int[sentences.length];
		for (int i = 0; i < sentences.length; i++) {
			prediction[i] = mapKnowledgeTypeToIndex(predict(sentences[i]));
		}
		double scoreTime = (System.nanoTime() - start) / 1E6;
		return calculateEvaluationMetrics(truth, prediction, fitTime, scoreTime);
	}

	private Map<String, ClassificationMetrics> calculateEvaluationMetrics(int[] truth, int[] prediction, double fitTime,
			double scoreTime) {
		Map<String, ClassificationMetrics> resultsMap = new LinkedHashMap<>();
		ClassificationValidation<Classifier<double[]>> validationOverall = new ClassificationValidation<Classifier<double[]>>(
				model, truth, prediction, fitTime, scoreTime);
		resultsMap.put("Fine-grained Overall", validationOverall.metrics);

		for (int classLabel = 0; classLabel < numClasses; classLabel++) {
			KnowledgeType type = FineGrainedClassifier.mapIndexToKnowledgeType(classLabel);
			int[] binaryTruth = mapFineGrainedToBinaryResults(truth, classLabel);
			int[] binaryPredictions = mapFineGrainedToBinaryResults(prediction, classLabel);

			ClassificationValidation<Classifier<double[]>> validation = new ClassificationValidation<Classifier<double[]>>(
					model, binaryTruth, binaryPredictions, fitTime, scoreTime);

			resultsMap.put("Fine-grained " + type.toString(), validation.metrics);
		}
		return resultsMap;
	}

	private int[] mapFineGrainedToBinaryResults(int[] multiClassResults, int label) {
		Integer[] binaryResults = Arrays.stream(ArrayUtils.toObject(multiClassResults))
				.map(x -> x.equals(label) ? 1 : 0).toArray(Integer[]::new);
		return ArrayUtils.toPrimitive(binaryResults);
	}

	/**
	 * @param sentences
	 *            for that the {@link KnowledgeType} should be predicted. The
	 *            sentences need to be relevant wrt. decision knowledge. The
	 *            relevance is determined by the {@link BinaryClassifier}.
	 * @return predicted {@link KnowledgeType}s as a list in the same order as the
	 *         input sentences.
	 */
	public List<KnowledgeType> predict(List<String> sentences) {
		List<KnowledgeType> fineGrainedPredictionResults = new ArrayList<>();
		for (String string : sentences) {
			fineGrainedPredictionResults.add(predict(string));
		}
		return fineGrainedPredictionResults;
	}

	/**
	 * @param sentence
	 *            for that the {@link KnowledgeType} should be predicted. The
	 *            sentence needs to be relevant wrt. decision knowledge. The
	 *            relevance is determined by the {@link BinaryClassifier}.
	 * @return predicted {@link KnowledgeType}, i.e. whether sentence is a decision,
	 *         an issue, an alternative, a pro-, or a con-argument.
	 */
	public KnowledgeType predict(String sentence) {
		double[][] sample = Preprocessor.getInstance().preprocess(sentence);
		// Make prediction for each nGram and determine most frequent prediction
		// result.
		int predictionResults[] = new int[sample.length];
		for (int i = 0; i < sample.length; i++) {
			predictionResults[i] = predict(sample[i]);
		}
		int predictionResult = mode(predictionResults);
		KnowledgeType predictedKnowledgeType = FineGrainedClassifier.mapIndexToKnowledgeType(predictionResult);
		return predictedKnowledgeType;
	}

	public void update(double[][] trainingInstances, KnowledgeType label) {
		super.update(trainingInstances, mapKnowledgeTypeToIndex(label));
	}

	/**
	 * @param index
	 *            that represents the {@link KnowledgeType}.
	 * @return {@link KnowledgeType} for the integer label. For example, if a
	 *         sentence is labeled with 3, it is of {@link KnowledgeType#DECISION}.
	 */
	public static KnowledgeType mapIndexToKnowledgeType(int index) {
		switch (index) {
		case 0:
			return KnowledgeType.ALTERNATIVE;
		case 1:
			return KnowledgeType.PRO;
		case 2:
			return KnowledgeType.CON;
		case 3:
			return KnowledgeType.DECISION;
		case 4:
			return KnowledgeType.ISSUE;
		default:
			return KnowledgeType.OTHER;
		}
	}

	/**
	 * @param knowledgeType
	 *            {@link KnowledgeType} that should be converted into an integer
	 *            label.
	 * @return integer label for the {@link KnowledgeType}. For example, if a
	 *         sentence is of {@link KnowledgeType#DECISION}, it is labeled with 3.
	 */
	public static int mapKnowledgeTypeToIndex(KnowledgeType knowledgeType) {
		switch (knowledgeType) {
		case ALTERNATIVE:
			return 0;
		case PRO:
			return 1;
		case CON:
			return 2;
		case DECISION:
			return 3;
		case ISSUE:
			return 4;
		default:
			return -1;
		}
	}

}
