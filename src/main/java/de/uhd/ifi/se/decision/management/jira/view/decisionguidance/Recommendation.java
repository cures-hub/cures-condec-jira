package de.uhd.ifi.se.decision.management.jira.view.decisionguidance;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.KnowledgeSourceType;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;

@XmlRootElement(name = "Recommendation")
public class Recommendation {

	@XmlElement
	protected String knowledgeSourceName;

	@XmlElement
	protected String recommendations;

	protected KnowledgeSourceType knowledgeSourceType;

	@XmlElement
	protected String url;

	protected int score;

	public Recommendation() {

	}

	public Recommendation(String knowledgeSourceName, String recommendations, KnowledgeSourceType knowledgeSourceType, String url) {
		this.knowledgeSourceName = knowledgeSourceName;
		this.recommendations = recommendations;
		this.knowledgeSourceType = knowledgeSourceType;
		this.url = url;
	}

	public String getKnowledgeSourceName() {
		return knowledgeSourceName;
	}

	public void setKnowledgeSourceName(String knowledgeSourceName) {
		this.knowledgeSourceName = knowledgeSourceName;
	}

	public String getRecommendations() {
		return recommendations;
	}

	public void setRecommendations(String recommendations) {
		this.recommendations = recommendations;
	}

	public KnowledgeSourceType getKnowledgeSourceType() {
		return knowledgeSourceType;
	}

	public void setKnowledgeSourceType(KnowledgeSourceType knowledgeSourceType) {
		this.knowledgeSourceType = knowledgeSourceType;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@XmlElement(name = "score")
	public int getScore() {
		return 0;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Recommendation that = (Recommendation) o;
		return knowledgeSourceName.equals(that.knowledgeSourceName) &&
			recommendations.equals(that.recommendations);
	}

	@Override
	public int hashCode() {
		return Objects.hash(knowledgeSourceName, recommendations);
	}
}
