package de.uhd.ifi.se.decision.management.jira.classification.preprocessing;

import java.util.ArrayList;
import java.util.List;

import de.uhd.ifi.se.decision.management.jira.classification.TrainingData;

public class PreprocessedData {

	public List<double[]> preprocessedSentences;
	public int[] updatedLabels;
	public Preprocessor preprocessor;

	public PreprocessedData(TrainingData trainingData, boolean isFineGrained) {
		preprocessor = Preprocessor.getInstance();
		if (isFineGrained) {
			int size = trainingData.relevantSentences.length;
			preprocessedSentences = new ArrayList<>();
			updatedLabels = new int[size];
			preprocess(trainingData.relevantSentences, trainingData.labelsKnowledgeType);
		} else {
			int size = trainingData.sentences.length;
			preprocessedSentences = new ArrayList<>();
			updatedLabels = new int[size];
			preprocess(trainingData.sentences, trainingData.labelsIsRelevant);
		}
	}

	/**
	 * Preprocesses sentences in such a way, that the classifiers can use them for
	 * training or prediction. The labels are used when to have the correct labels
	 * for each feature-set. E.g.: One sentence is preprocessed to multiple N-grams.
	 * To get the correct mapping of features to labels the label list is augmented.
	 *
	 * @param stringsToBePreprocessed
	 *            sentences
	 * @param labels
	 *            labels of the sentences
	 */
	private void preprocess(String[] stringsToBePreprocessed, int[] labels) {
		for (int i = 0; i < stringsToBePreprocessed.length; i++) {
			List<double[]> preprocessedSentence;
			try {
				preprocessedSentence = preprocessor.preprocess(stringsToBePreprocessed[i]);
				for (int _i = 0; _i < preprocessedSentence.size(); _i++) {
					updatedLabels[i] = labels[i];
				}
				preprocessedSentences.addAll(preprocessedSentence);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
