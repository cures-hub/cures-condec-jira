package de.uhd.ifi.se.decision.management.jira.extraction.impl;

import de.uhd.ifi.se.decision.management.jira.extraction.Classifier;
import smile.classification.SVM;
import smile.math.kernel.MercerKernel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BinaryClassifierImplementation extends Classifier {

    public static final String DEFAULT_MODEL_NAME = "binaryClassifier.model";

    public BinaryClassifierImplementation(Integer numClasses) {
        super(numClasses);
    }

    public BinaryClassifierImplementation(SVM<Double[]> svm, Integer numClasses) {
        super(svm, numClasses);
    }

    public BinaryClassifierImplementation(SVM<Double[]> svm, Integer epochs, Integer numClasses) {
        super(svm, epochs, numClasses);
    }

    public BinaryClassifierImplementation(Double c, Double sigma, Integer epochs, Integer numClasses) {
        super(c, sigma, epochs, numClasses);
    }

    public BinaryClassifierImplementation(Double c, MercerKernel kernel, Integer epochs, Integer numClasses) {
        super(c, kernel, epochs, numClasses);
    }

    public BinaryClassifierImplementation(MercerKernel kernel, SVM<Double[]> model, Integer epochs, Integer numClasses) {
        super(kernel, model, epochs, numClasses);
    }

    boolean predictIsKnowledge(Double[] feature) throws Exception {
        // If the probability for being relevant is greater than 0.5, true is returned.
        return super.predictProbabilities(feature)[1] > 0.5;
    }

    @Override
    public void train(File trainingFile) {
        List<Double[]> features = new ArrayList<>();
        List<Integer> labels = new ArrayList<>();
        //TODO!
        super.train((Double[][]) features.toArray(), (Integer[]) labels.toArray());
    }

    public void loadFromFile() throws Exception {
        super.loadFromFile(Classifier.DEFAULT_PATH + BinaryClassifierImplementation.DEFAULT_MODEL_NAME);
    }

    public void saveToFile() throws Exception {
        super.saveToFile(Classifier.DEFAULT_PATH + BinaryClassifierImplementation.DEFAULT_MODEL_NAME);
    }
}
