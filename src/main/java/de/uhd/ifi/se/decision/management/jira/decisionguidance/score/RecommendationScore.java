package de.uhd.ifi.se.decision.management.jira.decisionguidance.score;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

public class RecommendationScore {

	private float scoreValue;
	private String explanation;
	private List<RecommendationScore> score;


	public RecommendationScore(float scoreValue, String explanation) {
		this.scoreValue = scoreValue;
		this.explanation = explanation;
		this.score = new ArrayList<>();
	}

	@XmlElement(name = "score")
	public float getScoreValue() {
		return scoreValue;
	}

	public void setScoreValue(float scoreValue) {
		this.scoreValue = scoreValue;
	}

	@XmlElement(name = "explanation")
	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	public void composeScore(RecommendationScore score) {
		this.score.add(score);
	}

	public void setComposedScore(List<RecommendationScore> recommendationScores) {
		this.score.addAll(recommendationScores);
	}

	@XmlElement(name = "composedScore")
	public List<RecommendationScore> getComposeDScore() {
		return this.score;
	}


}
