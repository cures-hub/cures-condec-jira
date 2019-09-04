package de.uhd.ifi.se.decision.management.jira.extraction.impl;

import de.uhd.ifi.se.decision.management.jira.extraction.Classifier;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import smile.classification.SVM;
import smile.math.kernel.MercerKernel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FineGrainedClassifierImpl extends Classifier {

    public static final String DEFAULT_MODEL_NAME = "fineGrainedClassifier.model";

    public FineGrainedClassifierImpl(Integer numClasses) {
        super(numClasses);
    }

    public FineGrainedClassifierImpl(SVM<Double[]> svm, Integer numClasses) {
        super(svm, numClasses);
    }

    public FineGrainedClassifierImpl(SVM<Double[]> svm, Integer epochs, Integer numClasses) {
        super(svm, epochs, numClasses);
    }

    public FineGrainedClassifierImpl(Double c, Double sigma, Integer epochs, Integer numClasses) {
        super(c, sigma, epochs, numClasses);
    }

    public FineGrainedClassifierImpl(Double c, MercerKernel kernel, Integer epochs, Integer numClasses) {
        super(c, kernel, epochs, numClasses);
    }

    public FineGrainedClassifierImpl(MercerKernel kernel, SVM<Double[]> model, Integer epochs, Integer numClasses) {
        super(kernel, model, epochs, numClasses);
    }

    KnowledgeType predictKnowledgeType(Double[] feature){
        //TODO!
        return KnowledgeType.OTHER;
    }

    @Override
    public void train(File trainingFile) {
        List<Double[]> features = new ArrayList<>();
        List<Integer> labels = new ArrayList<>();
        //TODO!

        super.train((Double[][]) features.toArray(), (Integer[]) labels.toArray());
    }

    @Override
    public void saveToFile() throws Exception {
        super.saveToFile(Classifier.DEFAULT_PATH + FineGrainedClassifierImpl.DEFAULT_MODEL_NAME);
    }

    @Override
    public void loadFromFile() throws Exception {
        super.loadFromFile(Classifier.DEFAULT_PATH + FineGrainedClassifierImpl.DEFAULT_MODEL_NAME);
    }
}
