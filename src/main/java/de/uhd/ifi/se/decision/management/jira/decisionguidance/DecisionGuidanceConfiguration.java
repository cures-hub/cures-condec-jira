package de.uhd.ifi.se.decision.management.jira.decisionguidance;

import org.codehaus.jackson.annotate.JsonProperty;

public class DecisionGuidanceConfiguration {

	private boolean isRecommendationAddedToKnowledgeGraph;
	private int maxNumberOfRecommendations;
	private double similarityThreshold;
	private String irrelevantWords;

	public DecisionGuidanceConfiguration() {
		this.setRecommendationAddedToKnowledgeGraph(false);
		this.setMaxNumberOfRecommendations(100);
		this.setSimilarityThreshold(0.85);
		this.setIrrelevantWords("");
	}

	public boolean isRecommendationAddedToKnowledgeGraph() {
		return isRecommendationAddedToKnowledgeGraph;
	}

	@JsonProperty
	public void setRecommendationAddedToKnowledgeGraph(boolean isRecommendationAddedToKnowledgeGraph) {
		this.isRecommendationAddedToKnowledgeGraph = isRecommendationAddedToKnowledgeGraph;
	}

	public int getMaxNumberOfRecommendations() {
		return maxNumberOfRecommendations;
	}

	@JsonProperty
	public void setMaxNumberOfRecommendations(int maxNumberOfRecommendations) {
		this.maxNumberOfRecommendations = maxNumberOfRecommendations;
	}

	public double getSimilarityThreshold() {
		return similarityThreshold;
	}

	@JsonProperty
	public void setSimilarityThreshold(double similarityThreshold) {
		this.similarityThreshold = similarityThreshold;
	}

	public String getIrrelevantWords() {
		return irrelevantWords;
	}

	@JsonProperty
	public void setIrrelevantWords(String irrelevantWords) {
		this.irrelevantWords = irrelevantWords;
	}

}
