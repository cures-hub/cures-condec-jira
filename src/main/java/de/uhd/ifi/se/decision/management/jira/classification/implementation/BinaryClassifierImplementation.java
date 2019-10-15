package de.uhd.ifi.se.decision.management.jira.classification.implementation;

import de.uhd.ifi.se.decision.management.jira.classification.Classifier;

public class BinaryClassifierImplementation extends Classifier {

    public static final String DEFAULT_MODEL_NAME = "binaryClassifier.model";

    public BinaryClassifierImplementation() {
        super(2);
    }


    public boolean loadFromFile() {
        return super.loadFromFile(Classifier.DEFAULT_PATH + BinaryClassifierImplementation.DEFAULT_MODEL_NAME);
    }

    public void saveToFile() throws Exception {
        super.saveToFile(Classifier.DEFAULT_PATH + BinaryClassifierImplementation.DEFAULT_MODEL_NAME);
    }

    public boolean isRelevant(Double[] probabilities) {
        return probabilities[1] > 0.5;
    }
}
