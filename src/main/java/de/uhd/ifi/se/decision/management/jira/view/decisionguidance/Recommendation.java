package de.uhd.ifi.se.decision.management.jira.view.decisionguidance;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;

@XmlRootElement(name = "Recommendation")
public class Recommendation {

	@XmlElement
	protected String knowledgeSourceName;

	@XmlElement
	protected String recommendations;

	@XmlElement
	protected String url;

	@XmlElement
	protected int score;

	public Recommendation() {

	}

	public Recommendation(String knowledgeSourceName, String recommendations, String url) {
		this.knowledgeSourceName = knowledgeSourceName;
		this.recommendations = recommendations;
		this.url = url;
	}

	public Recommendation(String knowledgeSourceName, String recommendations, int score, String url) {
		this.knowledgeSourceName = knowledgeSourceName;
		this.recommendations = recommendations;
		this.score = score;
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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getScore() {
		return this.score;
	}

	public void setScore(int score) {
		this.score = score;
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
