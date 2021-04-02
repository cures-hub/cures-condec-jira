package de.uhd.ifi.se.decision.management.jira.decisionguidance;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.projectsource.ProjectSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.rdfsource.RDFSource;

public class DecisionGuidanceConfiguration {

	private boolean isRecommendationAddedToKnowledgeGraph;
	private int maxNumberOfRecommendations;
	private double similarityThreshold;
	private String irrelevantWords;
	private List<RDFSource> rdfKnowledgeSources;
	private List<ProjectSource> projectKnowledgeSources;

	public DecisionGuidanceConfiguration() {
		this.setRecommendationAddedToKnowledgeGraph(false);
		this.setMaxNumberOfRecommendations(100);
		this.setSimilarityThreshold(0.85);
		this.setIrrelevantWords("");
		this.rdfKnowledgeSources = new ArrayList<>();
		this.projectKnowledgeSources = new ArrayList<>();
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

	public List<RDFSource> getRdfKnowledgeSources() {
		return rdfKnowledgeSources;
	}

	@JsonProperty
	public void setRdfKnowledgeSources(List<RDFSource> rdfKnowledgeSources) {
		this.rdfKnowledgeSources = rdfKnowledgeSources;
	}

	public List<ProjectSource> getProjectKnowledgeSources() {
		return projectKnowledgeSources;
	}

	@JsonProperty
	public void setProjectKnowledgeSources(List<ProjectSource> projectKnowledgeSources) {
		this.projectKnowledgeSources = projectKnowledgeSources;
	}

}
