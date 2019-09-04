package de.uhd.ifi.se.decision.management.jira.extraction.impl;

import de.uhd.ifi.se.decision.management.jira.extraction.Classifier;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import smile.classification.SVM;
import smile.math.kernel.MercerKernel;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
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

    KnowledgeType predictKnowledgeType(Double[] feature) throws Exception {
        Double[] probabilities = super.predictProbabilities(feature);
        int maxAt = -1;
        for (int i = 0; i < probabilities.length; i++) {
            maxAt = probabilities[i] > probabilities[maxAt] ? i : maxAt;
        }
        return this.mapIndexToKnowledgeType(maxAt);
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

    private KnowledgeType mapIndexToKnowledgeType(int index){
        switch (index){
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
        /*
        case ALTERNATIVE:
                instance.setValue(0, 1);
                break;
            case PRO:
                instance.setValue(1, 1);
                break;
            case CON:
                instance.setValue(2, 1);
                break;
            case DECISION:
                instance.setValue(3, 1);
                break;
            case ISSUE:
                instance.setValue(4, 1);
                break;
            default:
                break;
         */
    }
}
