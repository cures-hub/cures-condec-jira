package de.uhd.ifi.se.decision.management.jira.classification;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.PreprocessedData;
import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.Preprocessor;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class FineGrainedClassifier extends AbstractClassifier {

	public static final String DEFAULT_MODEL_NAME = "fineGrainedClassifier.model";

	public FineGrainedClassifier(int numClasses) {
		super(numClasses);
	}

	/**
	 * Trains the model.
	 *
	 * @param trainingData
	 */
	public void train(TrainingData trainingData) {
		PreprocessedData preprocessedData = new PreprocessedData(trainingData, true);
		train(preprocessedData.preprocessedSentences, preprocessedData.updatedLabels);
	}

	public KnowledgeType predict(String stringToBeClassified) {
		double[][] sample = Preprocessor.getInstance().preprocess(stringToBeClassified);
		// Make predictions for each nGram; then determine maximum probability of all
		// added together.
		int predictionResults[] = new int[sample.length];
		for (int i = 0; i < sample.length; i++) {
			predictionResults[i] = predict(sample[i]);
		}
		int predictionResult = (int) Math.round(DecisionKnowledgeClassifier.median(predictionResults));
		KnowledgeType predictedKnowledgeType = FineGrainedClassifier.mapIndexToKnowledgeType(predictionResult);
		return predictedKnowledgeType;
	}

	/**
	 * @param stringsToBeClassified
	 * @return
	 * @see this.makeBinaryDecision
	 */
	public List<KnowledgeType> predict(List<String> stringsToBeClassified) {
		try {
			List<KnowledgeType> fineGrainedPredictionResults = new ArrayList<>();
			for (String string : stringsToBeClassified) {
				fineGrainedPredictionResults.add(predict(string));
			}
			return fineGrainedPredictionResults;
		} catch (Exception e) {
			LOGGER.error("Fine grained classification failed. Message: " + e.getMessage());
		}
		return null;
	}

	public static KnowledgeType getType(double[] classification) {
		return mapIndexToKnowledgeType(maxAtInArray(classification));
	}

	private static int maxAtInArray(double[] probabilities) {
		int maxAt = 0;
		for (int i = 0; i < probabilities.length; i++) {
			maxAt = probabilities[i] > probabilities[maxAt] ? i : maxAt;
		}
		return maxAt;
	}

	public void train(double[] feature, KnowledgeType label) {
		super.update(feature, mapKnowledgeTypeToIndex(label));
	}

	@Override
	public File saveToFile() {
		return super.saveToFile(
				DecisionKnowledgeClassifier.CLASSIFIER_DIRECTORY + FineGrainedClassifier.DEFAULT_MODEL_NAME);
	}

	@Override
	public boolean loadFromFile() {
		return super.loadFromFile(DecisionKnowledgeClassifier.CLASSIFIER_DIRECTORY + FineGrainedClassifier.DEFAULT_MODEL_NAME);
	}

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
