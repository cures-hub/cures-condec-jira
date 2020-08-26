package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;

import java.util.List;

public class Recommendation {

	String knowledgeSourceName;
	List<KnowledgeElement> recommendations;

	Recommendation(String knowledgeSourceName, List<KnowledgeElement> recommendations) {
		this.knowledgeSourceName = knowledgeSourceName;
		this.recommendations = recommendations;
	}

	public String getKnowledgeSourceName() {
		return knowledgeSourceName;
	}

	public void setKnowledgeSourceName(String knowledgeSourceName) {
		this.knowledgeSourceName = knowledgeSourceName;
	}

	public List<KnowledgeElement> getRecommendations() {
		return recommendations;
	}

	public void setRecommendations(List<KnowledgeElement> recommendations) {
		this.recommendations = recommendations;
	}
}
