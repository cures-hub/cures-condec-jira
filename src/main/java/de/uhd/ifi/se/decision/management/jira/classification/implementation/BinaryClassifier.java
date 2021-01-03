package de.uhd.ifi.se.decision.management.jira.classification.implementation;

import de.uhd.ifi.se.decision.management.jira.classification.AbstractClassifier;

public class BinaryClassifier extends AbstractClassifier {

	public static final String DEFAULT_MODEL_NAME = "binaryClassifier.model";

	public BinaryClassifier() {
		super(2);
	}

	@Override
	public boolean loadFromFile() {
		return super.loadFromFile(AbstractClassifier.DEFAULT_PATH + BinaryClassifier.DEFAULT_MODEL_NAME);
	}

	@Override
	public void saveToFile() throws Exception {
		super.saveToFile(AbstractClassifier.DEFAULT_PATH + BinaryClassifier.DEFAULT_MODEL_NAME);
	}

	public boolean isRelevant(double[] probabilities) {
		return probabilities[1] > 0.5;
	}
}
