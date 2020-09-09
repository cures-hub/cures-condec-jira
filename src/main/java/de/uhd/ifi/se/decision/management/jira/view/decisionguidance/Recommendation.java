package de.uhd.ifi.se.decision.management.jira.view.decisionguidance;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.KnowledgeSourceType;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Recommendation")
public class Recommendation {

	@XmlElement
	protected String knowledgeSourceName;

	@XmlElement
	protected KnowledgeElement recommendations;

	protected KnowledgeSourceType knowledgeSourceType;

	protected int score;

	public Recommendation() {

	}

	public Recommendation(String knowledgeSourceName, KnowledgeElement recommendations, KnowledgeSourceType knowledgeSourceType) {
		this.knowledgeSourceName = knowledgeSourceName;
		this.recommendations = recommendations;
		this.knowledgeSourceType = knowledgeSourceType;
	}

	public String getKnowledgeSourceName() {
		return knowledgeSourceName;
	}

	public void setKnowledgeSourceName(String knowledgeSourceName) {
		this.knowledgeSourceName = knowledgeSourceName;
	}

	public KnowledgeElement getRecommendations() {
		return recommendations;
	}

	public void setRecommendations(KnowledgeElement recommendations) {
		this.recommendations = recommendations;
	}

	public KnowledgeSourceType getKnowledgeSourceType() {
		return knowledgeSourceType;
	}

	public void setKnowledgeSourceType(KnowledgeSourceType knowledgeSourceType) {
		this.knowledgeSourceType = knowledgeSourceType;
	}

	@XmlElement(name = "score")
	public int getScore() {
		return 0;
	}

	public void setScore(int score) {
		this.score = score;
	}
}
