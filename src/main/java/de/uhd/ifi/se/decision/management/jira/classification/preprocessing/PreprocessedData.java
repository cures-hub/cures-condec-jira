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
		preprocessedSentences = new double[0][];
		updatedLabels = new int[0];
		preprocessor.preprocess(stringsToBePreprocessed[0]);
		for (int i = 0; i < stringsToBePreprocessed.length; i++) {
			double[][] preprocessedSentence = preprocessor.preprocess(stringsToBePreprocessed[i]);
			if (preprocessedSentence[0] == null) {
				continue;
			}
			int[] myLabels = new int[preprocessedSentence.length];
			for (int j = 0; j < preprocessedSentence.length; j++) {
				myLabels[j] = labels[i];
			}
			updatedLabels = concatenate(updatedLabels, myLabels);
			preprocessedSentences = concatenate(preprocessedSentences, preprocessedSentence);
		}
	}

	/**
	 * @param a
	 *            first array of double values
	 * @param b
	 *            second array of double values
	 * @return concatenated array of double values
	 */
	public static int[] concatenate(int[] a, int[] b) {
		int aLen = a.length;
		int bLen = b.length;
		int[] c = new int[aLen + bLen];
		System.arraycopy(a, 0, c, 0, aLen);
		System.arraycopy(b, 0, c, aLen, bLen);
		return c;
	}

	/**
	 * @param a
	 *            first array of double values
	 * @param b
	 *            second array of double values
	 * @return concatenated array of double values
	 */
	public static double[][] concatenate(double[][] a, double[][] b) {
		int aLen = a.length;
		int bLen = b.length;
		double[][] c = new double[aLen + bLen][];
		System.arraycopy(a, 0, c, 0, aLen);
		System.arraycopy(b, 0, c, aLen, bLen);
		return c;
	}
}
