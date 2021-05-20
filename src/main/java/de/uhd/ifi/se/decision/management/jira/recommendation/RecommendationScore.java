package de.uhd.ifi.se.decision.management.jira.recommendation;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

/**
 * The score represents the predicted relevance of a {@link Recommendation},
 * i.e., how likely it is that the user accepts the recommendation. The score
 * can be used to rank/sort the recommendations.
 * 
 * The score consists of a value and an explanation. Besides, the score can be
 * composed of various sub-scores ({@link this#getComposedScores()}) for the
 * criteria that were used to calculate the score.
 */
public class RecommendationScore {

	private float value;
	private String explanation;
	private List<RecommendationScore> subScores;

	public RecommendationScore() {
		this.explanation = "";
		this.subScores = new ArrayList<>();
	}

	public RecommendationScore(float totalScore, String explanation) {
		this();
		this.value = totalScore;
		this.explanation = explanation;
	}

	/**
	 * @return score value. If the score is composed of sub-scores, the value is
	 *         calculated using these sub-scores. It needs to be normalized to
	 *         enable recommendation ranking.
	 */
	@XmlElement
	public float getValue() {
		return value > 0 ? value : getSumOfSubScores();
	}

	/**
	 * @param value
	 *            of the score. If the score is composed of sub-scores, the value is
	 *            calculated using these sub-scores. It might be normalized to
	 *            enable recommendation ranking.
	 */
	public void setValue(float value) {
		this.value = value;
	}

	/**
	 * @return sum of all the sub-score values.
	 */
	public float getSumOfSubScores() {
		float sumOfSubScoreValues = 0;
		for (RecommendationScore subScore : subScores) {
			sumOfSubScoreValues += subScore.getValue();
		}
		return sumOfSubScoreValues;
	}

	/**
	 * Normalizes the score value against the value of the best recommendation. Adds
	 * a sub-score for the absolute score value of this recommendation and another
	 * sub-score for the absolute score value of the best recommendation.
	 * 
	 * @param maxScoreValue
	 *            of the best recommendation, set to 100%.
	 */
	public void normalizeTo(float maxScoreValue) {
		float oldValue = getValue();
		// keep the current explanation as a sub-score after normalizing
		addSubScore(new RecommendationScore(oldValue, explanation + " of this recommendation"));
		addSubScore(new RecommendationScore(maxScoreValue, explanation + " of the best recommendation"));
		// normalize, update score value and explanation
		value = (oldValue * 1.0f / maxScoreValue) * 100f;
		explanation = "Compared to best recommendation (normalized)";
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
	 * @param subScore
	 *            sub-score which this score is composed of.
	 */
	public void addSubScore(RecommendationScore subScore) {
		this.subScores.add(subScore);
	}

	/**
	 * @param subScores
	 *            sub-scores which this score is composed of.
	 */
	public void setSubScores(List<RecommendationScore> subScores) {
		this.subScores = subScores;
	}

	/**
	 * @return sub-scores which this score is composed of.
	 */
	@XmlElement
	public List<RecommendationScore> getSubScores() {
		return subScores;
	}
}