package de.uhd.ifi.se.decision.management.jira.classification;

import java.util.ArrayList;
import java.util.List;

public class TrainingData {
	public List<String> sentences;
	public List<String> relevantSentences;
	public List<Integer> labelsIsRelevant;
	public List<Integer> labelsKnowledgeType;

	public TrainingData() {
		sentences = new ArrayList<>();
		relevantSentences = new ArrayList<>();
		labelsIsRelevant = new ArrayList<>();
		labelsKnowledgeType = new ArrayList<>();
	}

	//
	// extractedTrainingData.put("sentences", sentences);
	// extractedTrainingData.put("relevantSentences", relevantSentences);
	//
	// extractedTrainingData.put("labelsIsRelevant", labelsIsRelevant);
	// extractedTrainingData.put("labelKnowledgeType", labelsKnowledgeType);

}
