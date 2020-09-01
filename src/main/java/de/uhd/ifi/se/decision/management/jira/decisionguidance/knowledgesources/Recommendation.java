package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "Recommendation")
@XmlAccessorType(XmlAccessType.FIELD)
public class Recommendation {

	@XmlElement
	String knowledgeSourceName;

	@XmlElement
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
