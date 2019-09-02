package de.uhd.ifi.se.decision.management.jira.extraction;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

import java.util.List;

public interface OnlineClassificationTrainer {

    public void train();

    public KnowledgeType predict();

    public List<Double> executePerformanceCheck();

    public void persistClassifier();

    public void loadClassifier();
}
