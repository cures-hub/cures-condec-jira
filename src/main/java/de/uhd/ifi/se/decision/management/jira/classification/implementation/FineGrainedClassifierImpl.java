package de.uhd.ifi.se.decision.management.jira.classification.implementation;

import de.uhd.ifi.se.decision.management.jira.classification.AbstractClassifier;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class FineGrainedClassifierImpl extends AbstractClassifier {

    public static final String DEFAULT_MODEL_NAME = "fineGrainedClassifier.model";

    public FineGrainedClassifierImpl(Integer numClasses) {
        super(numClasses);
    }

    public int maxAtInArray(Double[] probabilities) {
        int maxAt = 0;
        for (int i = 0; i < probabilities.length; i++) {
            maxAt = probabilities[i] > probabilities[maxAt] ? i : maxAt;
        }
        return maxAt;
    }

    public void train(Double[] feature, KnowledgeType label) {
        super.train(feature, mapKnowledgeTypeToIndex(label));
    }


    @Override
    public void saveToFile() throws Exception {
        super.saveToFile(AbstractClassifier.DEFAULT_PATH + FineGrainedClassifierImpl.DEFAULT_MODEL_NAME);
    }

    @Override
    public boolean loadFromFile() {
        return super.loadFromFile(AbstractClassifier.DEFAULT_PATH + FineGrainedClassifierImpl.DEFAULT_MODEL_NAME);
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
