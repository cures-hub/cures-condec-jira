package de.uhd.ifi.se.decision.management.jira.classification.preprocessing;

import de.uhd.ifi.se.decision.management.jira.classification.TrainingData;

public class PreprocessedData {

	public double[][] preprocessedSentences;
	public int[] updatedLabels;
	public Preprocessor preprocessor;

	public PreprocessedData(TrainingData trainingData, boolean isFineGrained) {
		preprocessor = Preprocessor.getInstance();
		if (isFineGrained) {
			int size = trainingData.relevantSentences.length;
			preprocessedSentences = new double[size][];
			updatedLabels = new int[size];
			preprocess(trainingData.relevantSentences, trainingData.labelsKnowledgeType);
		} else {
			int size = trainingData.sentences.length;
			preprocessedSentences = new double[size][];
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
			try {
				double[][] preprocessedSentence = preprocessor.preprocess(stringsToBePreprocessed[i]);
				for (int j = 0; j < preprocessedSentence.length; j++) {
					updatedLabels[i] = labels[i];
				}
				preprocessedSentences[i] = new double[preprocessedSentence.length];
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
