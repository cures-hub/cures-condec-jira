package de.uhd.ifi.se.decision.management.jira.classification;

import java.io.File;

public class BinaryClassifier extends AbstractClassifier {

	public static final String DEFAULT_MODEL_NAME = "binaryClassifier.model";

	public BinaryClassifier() {
		super(2);
	}

	public boolean isSentenceRelevant(String sentenceToBeClassified) {
		// TODO Implement binary classification here
		return true;
	}

	@Override
	public boolean loadFromFile() {
		return super.loadFromFile(DecisionKnowledgeClassifier.CLASSIFIER_DIRECTORY + BinaryClassifier.DEFAULT_MODEL_NAME);
	}

	@Override
	public File saveToFile() {
		return super.saveToFile(DecisionKnowledgeClassifier.CLASSIFIER_DIRECTORY + BinaryClassifier.DEFAULT_MODEL_NAME);
	}

	public static boolean isRelevant(double[] probabilities) {
		return probabilities[1] > 0.5;
	}
}
