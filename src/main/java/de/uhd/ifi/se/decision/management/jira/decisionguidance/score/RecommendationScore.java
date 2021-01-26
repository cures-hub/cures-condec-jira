package de.uhd.ifi.se.decision.management.jira.decisionguidance.score;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

/**
 *  A RecommendationScore consists of a total score value and an explanation.
 */
public class RecommendationScore {

	private float totalScore;
	private String explanation;
	private List<RecommendationScore> partScores;


	public RecommendationScore(float totalScore, String explanation) {
		this.totalScore = totalScore;
		this.explanation = explanation;
		this.partScores = new ArrayList<>();
	}

	@XmlElement(name = "totalScore")
	public float getTotalScore() {
		return totalScore;
	}

	public void setTotalScore(float totalScore) {
		this.totalScore = totalScore;
	}

	@XmlElement(name = "explanation")
	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	/**
	 * Adds the part scores which composes the total score.
	 * @param score
	 */
	public void composeScore(RecommendationScore score) {
		this.partScores.add(score);
	}

	public void setComposedScore(List<RecommendationScore> recommendationScores) {
		this.partScores.addAll(recommendationScores);
	}

	@XmlElement(name = "partScores")
	public List<RecommendationScore> getComposeDScore() {
		return this.partScores;
	}


}
