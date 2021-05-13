package de.uhd.ifi.se.decision.management.jira.decisionguidance;

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

	public RecommendationScore(float totalScore, String explanation) {
		this.value = totalScore;
		this.explanation = explanation;
		this.subScores = new ArrayList<>();
	}

	/**
	 * @return score value. If the score is composed of sub-scores, the value is
	 *         calculated using these sub-scores. It might be normalized to enable
	 *         recommendation ranking.
	 */
	@XmlElement
	public float getValue() {
		if (!subScores.isEmpty()) {
			value = 0;
			for (RecommendationScore subScore : subScores) {
				value += subScore.getValue();
			}
		}
		return value;
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
	public void setSubScore(List<RecommendationScore> subScores) {
		this.subScores.addAll(subScores);
	}

	/**
	 * @return sub-scores which this score is composed of.
	 */
	@XmlElement
	public List<RecommendationScore> getSubScores() {
		return subScores;
	}
}