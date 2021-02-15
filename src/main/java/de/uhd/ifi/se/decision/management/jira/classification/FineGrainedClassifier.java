package de.uhd.ifi.se.decision.management.jira.classification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.atlassian.gzipfilter.org.apache.commons.lang.ArrayUtils;

import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.PreprocessedData;
import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.Preprocessor;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.PartOfJiraIssueText;
import smile.classification.Classifier;
import smile.classification.LogisticRegression;
import smile.validation.ClassificationValidations;
import smile.validation.CrossValidation;
import smile.validation.metric.Precision;
import smile.validation.metric.Sensitivity;

/**
 * Fine grained classifier that predicts the {@link KnowledgeType} of a sentence
 * (i.e. {@link PartOfJiraIssueText}).
 * 
 * Extends the {@link AbstractClassifier} and is used in the
 * {@link TextClassifier}.
 */
public class FineGrainedClassifier extends AbstractClassifier {

	public FineGrainedClassifier(int numClasses) {
		super(numClasses);
	}

	@Override
	public String getName() {
		return "fineGrainedClassifier.model";
	}

	/**
	 * Trains the fine grained classifier.
	 *
	 * @param trainingData
	 *            {@link TrainingData} read from csv file (see
	 *            {@link #readDataFrameFromCSVFile(File)} or created from the
	 *            current {@link KnowledgeGraph).
	 */
	@Override
	public void train(TrainingData trainingData) {
		isCurrentlyTraining = true;
		PreprocessedData preprocessedData = new PreprocessedData(trainingData, true);
		model = train(preprocessedData.preprocessedSentences, preprocessedData.updatedLabels);
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
	public Map<String, Double> evaluateClassifier(int k, TrainingData groundTruthData) {
		Map<String, Double> resultsMap = new LinkedHashMap<>();
		PreprocessedData preprocessedData = new PreprocessedData(groundTruthData, true);
		ClassificationValidations<Classifier<double[]>> validations = CrossValidation.classification(k,
				preprocessedData.preprocessedSentences, preprocessedData.updatedLabels, this::train);
		resultsMap.put("Fine-grained Accuracy Overall", validations.avg.accuracy);
		resultsMap.put("Fine-grained Number of Errors Overall", (double) validations.avg.error);

		int[] truthsPrimitive = new int[0];
		int[] predictionsPrimitive = new int[0];
		for (int i = 0; i < k; i++) {
			truthsPrimitive = PreprocessedData.concatenate(truthsPrimitive, validations.rounds.get(0).truth);
			predictionsPrimitive = PreprocessedData.concatenate(predictionsPrimitive,
					validations.rounds.get(0).prediction);
		}

		for (int classLabel = 0; classLabel < TextClassifier.getInstance().getFineGrainedClassifier()
				.getNumClasses(); classLabel++) {
			KnowledgeType type = FineGrainedClassifier.mapIndexToKnowledgeType(classLabel);
			Integer[] truths = mapFineGrainedToBinaryResults(ArrayUtils.toObject(truthsPrimitive), classLabel);
			Integer[] predictions = mapFineGrainedToBinaryResults(ArrayUtils.toObject(predictionsPrimitive),
					classLabel);

			double fineGrainedPrecisions = Precision.of(ArrayUtils.toPrimitive(truths),
					ArrayUtils.toPrimitive(predictions));
			resultsMap.put("Fine-grained Precision " + type.toString(), fineGrainedPrecisions);
			double fineGrainedRecall = Sensitivity.of(ArrayUtils.toPrimitive(truths),
					ArrayUtils.toPrimitive(predictions));
			resultsMap.put("Fine-grained Recall " + type.toString(), fineGrainedRecall);
			resultsMap.put("Fine-grained F1 " + type.toString(),
					2 * (fineGrainedRecall * fineGrainedPrecisions) / (fineGrainedRecall + fineGrainedPrecisions));
		}
		return resultsMap;
	}

	private Integer[] mapFineGrainedToBinaryResults(Integer[] array, Integer currentElement) {
		return Arrays.stream(array).map(x -> x.equals(currentElement) ? 1 : 0).toArray(Integer[]::new);
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

	public void update(double[] feature, KnowledgeType label) {
		super.update(feature, mapKnowledgeTypeToIndex(label));
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
