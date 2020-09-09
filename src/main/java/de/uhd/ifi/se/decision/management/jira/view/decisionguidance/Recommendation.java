package de.uhd.ifi.se.decision.management.jira.view.decisionguidance;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Recommendation")
@XmlAccessorType(XmlAccessType.FIELD)
public class Recommendation {

	@XmlElement
	private String knowledgeSourceName;

	@XmlElement
	private KnowledgeElement recommendations;

	@XmlElement
	private int score;

	public Recommendation(String knowledgeSourceName, KnowledgeElement recommendations) {
		this.knowledgeSourceName = knowledgeSourceName;
		this.recommendations = recommendations;
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
}
