package de.uhd.ifi.se.decision.management.jira.extraction.impl;

import com.atlassian.gzipfilter.org.apache.commons.lang.ArrayUtils;
import de.uhd.ifi.se.decision.management.jira.extraction.Classifier;
import smile.classification.SVM;
import smile.math.kernel.MercerKernel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BinaryClassifierImplementation extends Classifier {

    public static final String DEFAULT_MODEL_NAME = "binaryClassifier.model";

    public BinaryClassifierImplementation() {
        super(2);
    }

    public BinaryClassifierImplementation(SVM<Double[]> svm) {
        super(svm, 2);
    }

    public BinaryClassifierImplementation(SVM<Double[]> svm, Integer epochs) {
        super(svm, epochs, 2);
    }

    public BinaryClassifierImplementation(Double c, Double sigma, Integer epochs) {
        super(c, sigma, epochs, 2);
    }

    public BinaryClassifierImplementation(Double c, MercerKernel kernel, Integer epochs) {
        super(c, kernel, epochs, 2);
    }

    public BinaryClassifierImplementation(MercerKernel kernel, SVM<Double[]> model, Integer epochs) {
        super(kernel, model, epochs, 2);
    }

    boolean predictIsKnowledge(Double[] feature) throws Exception {
        // If the probability for being relevant is greater than 0.5, true is returned.
        return this.isRelevant(ArrayUtils.toObject(super.predictProbabilities(feature)));
    }

    boolean predictIsKnowledge(List<Double> feature) throws Exception {
        // If the probability for being relevant is greater than 0.5, true is returned.
        return this.predictIsKnowledge(feature.toArray(Double[]::new));
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

    public boolean isRelevant (Double[] probabilities){
        return probabilities[1] > 0.5;
    }
}
