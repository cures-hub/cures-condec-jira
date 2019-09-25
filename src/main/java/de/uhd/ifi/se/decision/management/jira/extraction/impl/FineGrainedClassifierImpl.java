package de.uhd.ifi.se.decision.management.jira.extraction.impl;

import com.atlassian.gzipfilter.org.apache.commons.lang.ArrayUtils;
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

    public FineGrainedClassifierImpl(Double c, Integer degree, Integer epochs, Integer numClasses) {
        super(c, degree, epochs, numClasses);
    }

    public FineGrainedClassifierImpl(Double c, MercerKernel kernel, Integer epochs, Integer numClasses) {
        super(c, kernel, epochs, numClasses);
    }

    public FineGrainedClassifierImpl(MercerKernel kernel, SVM<Double[]> model, Integer epochs, Integer numClasses) {
        super(kernel, model, epochs, numClasses);
    }

    KnowledgeType predictKnowledgeType(Double[] feature) throws Exception {
        Double[] probabilities = ArrayUtils.toObject(super.predictProbabilities(feature));
        int maxAt = maxAtInArray(probabilities);
        return this.mapIndexToKnowledgeType(maxAt);
    }

    public int maxAtInArray(Double[] probabilities){
        int maxAt = 0;
        for (int i = 0; i < probabilities.length; i++) {
            maxAt = probabilities[i] > probabilities[maxAt] ? i : maxAt;
        }
        return maxAt;
    }

    KnowledgeType predictKnowledgeType(List<Double> feature) throws Exception {
        return this.predictKnowledgeType(feature.toArray(Double[]::new));
    }


    @Override
    public void train(File trainingFile) {
        List<Double[]> features = new ArrayList<>();
        List<Integer> labels = new ArrayList<>();
        //TODO!

        super.train((Double[][]) features.toArray(), (Integer[]) labels.toArray());
    }

    public void train(Double[] feature, KnowledgeType label) {
        super.train(feature, mapKnowledgeTypeToIndex(label));
    }


    @Override
    public void saveToFile() throws Exception {
        super.saveToFile(Classifier.DEFAULT_PATH + FineGrainedClassifierImpl.DEFAULT_MODEL_NAME);
    }

    @Override
    public void loadFromFile() throws Exception {
        super.loadFromFile(Classifier.DEFAULT_PATH + FineGrainedClassifierImpl.DEFAULT_MODEL_NAME);
    }

    public KnowledgeType mapIndexToKnowledgeType(int index) {
        switch (index) {
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
    }

    public Integer mapKnowledgeTypeToIndex(KnowledgeType knowledgeType) {
        switch (knowledgeType) {
            case ALTERNATIVE:
                return 0;
            case PRO:
                return 1;
            case CON:
                return 2;
            case DECISION:
                return 3;
            case ISSUE:
                return 4;
            default:
                break;
        }
        return -1;
    }
}
