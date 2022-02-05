package de.uhd.ifi.se.decision.management.jira.recommendation;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.ElementRecommendation;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.KnowledgeSource;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.LinkRecommendation;

/**
 * Interface for a recommendation of a {@link KnowledgeElement} element or
 * {@link Link}. Implementing classes are {@link ElementRecommendation} and
 * {@link LinkRecommendation}, respectively. Note that we use the words
 * recommendation and suggestion interchangeably.
 */
public interface Recommendation extends Comparable<Recommendation> {

	/**
	 * @return type of the recommendation, e.g. solution option from an
	 *         {@link RecommendationType#EXTERNAL} {@link KnowledgeSource} or a new
	 *         {@link RecommendationType#LINK} within the project.
	 */
	@XmlElement
	public abstract RecommendationType getRecommendationType();

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

	/**
	 * @param value
	 *            of the score (represents the confidence that the recommendation is
	 *            useful).
	 * @param explanation
	 *            on how the score was calculated.
	 */
	default void addToScore(double value, String explanation) {
		getScore().addSubScore(new RecommendationScore((float) value, explanation));
	}

	/**
	 * Normalizes the score values of all recommendations. Finds the best
	 * recommendation score and sets this score to 100%.
	 * 
	 * @param recommendations
	 *            list of {@link Recommendation}s.
	 * @return recommendations with normalized scores against the best
	 *         recommendation in the range of [0%, 100%].
	 */
	static List<Recommendation> normalizeRecommendationScore(List<Recommendation> recommendations) {
		float maxValue = getMaxScoreValue(recommendations);
		return normalizeRecommendationScore(maxValue, recommendations);
	}

	/**
	 * Normalizes the score values of all recommendations to a given max value and
	 * sets this score to 100%.
	 * 
	 * @param maxValue
	 *            is set to 100%.
	 * @param recommendations
	 *            list of {@link Recommendation}s.
	 * @return recommendations with normalized scores against the max value in the
	 *         range of [0%, 100%].
	 */
	static List<Recommendation> normalizeRecommendationScore(float maxValue, List<Recommendation> recommendations) {
		for (Recommendation recommendation : recommendations) {
			recommendation.getScore().normalizeTo(maxValue);
		}
		return recommendations;
	}

	/**
	 * @param recommendations
	 *            list of {@link Recommendation}s.
	 * @return value of the best scored {@link Recommendation}. Used to normalize
	 *         the {@link RecommendationScore}s of all recommendations in the range
	 *         of [0%, 100%].
	 */
	static float getMaxScoreValue(List<Recommendation> recommendations) {
		float maxScoreValue = 0;
		for (Recommendation recommendation : recommendations) {
			if (recommendation.getScore().getValue() > maxScoreValue) {
				maxScoreValue = recommendation.getScore().getValue();
			}
		}
		return maxScoreValue;
	}

	@XmlElement(name = "isDiscarded")
	boolean isDiscarded();

	void setDiscarded(boolean isDiscarded);

	/**
	 * Compares two {@link Recommendation}s based on their
	 * {@link RecommendationScore}.
	 */
	default int compareTo(Recommendation o) {
		if (o == null) {
			return -1;
		}
		int compareValue = 0;
		if (this.getScore().getValue() > o.getScore().getValue()) {
			compareValue = -1;
		} else {
			compareValue = 1;
		}
		return compareValue;
	}
}