package de.uhd.ifi.se.decision.management.jira.decisionguidance.score;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

/**
 * The score represents the predicted relevance of a recommendation, i.e., how
 * likely it is that the user accepts the recommendation. The score can be used
 * to rank/sort the recommendations.
 * 
 * The score can be composed of various criteria. It consists of a total score
 * value and an explanation.
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

	@XmlElement
	public float getTotalScore() {
		return totalScore;
	}

	public void setTotalScore(float totalScore) {
		this.totalScore = totalScore;
	}

	/**
	 * @return explanation on how the score was calculated.
	 */
	@XmlElement
	public String getExplanation() {
		return explanation;
	}

	/**
	 * @param explanation
	 *            on how the score was calculated.
	 */
	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	/**
	 * Adds the part scores which composes the total score.
	 * 
	 * @param score
	 */
	public void composeScore(RecommendationScore score) {
		this.partScores.add(score);
	}

	public void setComposedScore(List<RecommendationScore> recommendationScores) {
		this.partScores.addAll(recommendationScores);
	}

	@XmlElement(name = "partScores")
	public List<RecommendationScore> getComposedScores() {
		return partScores;
	}

}
