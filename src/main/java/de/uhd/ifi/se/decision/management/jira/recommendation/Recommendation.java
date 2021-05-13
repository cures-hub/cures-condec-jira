package de.uhd.ifi.se.decision.management.jira.recommendation;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.suggestions.SuggestionType;

public interface Recommendation extends Comparable<Recommendation> {

	/**
	 *
	 * @return suggestion type of suggestion
	 */
	public abstract SuggestionType getSuggestionType();

	/**
	 * @return score that represents the predicted relevance of a recommendation,
	 *         i.e., how likely it is that the user accepts the recommendation. The
	 *         score can be used to rank/sort the recommendations.
	 */
	@XmlElement
	RecommendationScore getScore();

	/**
	 * @param score
	 *            that represents the predicted relevance of a recommendation, i.e.,
	 *            how likely it is that the user accepts the recommendation. The
	 *            score can be used to rank/sort the recommendations.
	 */
	void setScore(RecommendationScore score);

	default void addToScore(double value, String field) {
		getScore().addSubScore(new RecommendationScore((float) value, field));
	}

	static float getMaxScoreValue(List<Recommendation> recommendations) {
		float maxScoreValue = 0;
		for (Recommendation recommendation : recommendations) {
			if (recommendation.getScore().getValue() > maxScoreValue) {
				maxScoreValue = recommendation.getScore().getValue();
			}
		}
		return maxScoreValue;
	}

	default int compareTo(Recommendation o) {
		if (o == null) {
			return -1;
		}
		int compareValue = 0;
		if (this.getScore().getValue() > o.getScore().getValue()) {
			compareValue = 1;
		} else {
			compareValue = -1;
		}
		return compareValue;
	}
}