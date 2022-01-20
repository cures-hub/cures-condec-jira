package de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.recommendation.RecommendationScore;

/**
 * Contains the configuration details for the link recommendation and duplicate
 * recognition for one Jira project (see {@link DecisionKnowledgeProject}).
 */
public class LinkRecommendationConfiguration {

	private double minProbability;

	/**
	 * Constructs an object with default values.
	 */
	public LinkRecommendationConfiguration() {
		this.minProbability = 0.8;
	}

	/**
	 * @return minimum {@link RecommendationScore} for link recommendation. To
	 *         calculate the link recommendations, the knowledge elements of a
	 *         project are used as a basis. Currently four context indicators are
	 *         used for the calculation: User, Time, Links, and Text. For each
	 *         indicator the values are scaled from 0.0 to 1.0. The value 1.0
	 *         indicates the most similar regarding the current context. Lastly, the
	 *         values are divided by the number of examined contexts to get scores
	 *         between 0.0 and 1.0.
	 */
	public double getMinProbability() {
		return minProbability;
	}

	/**
	 * @param minProbability
	 *            minimum score for link suggestion. To calculate the link
	 *            suggestions, the knowledge elements of a project are used as a
	 *            basis. Currently four context indicators are used for the
	 *            calculation: User, Time, Links, and Text. For each indicator the
	 *            values are scaled from 0.0 to 1.0. The value 1.0 indicates the
	 *            most similar regarding the current context. Lastly, the values are
	 *            divided by the number of examined contexts to get scores between
	 *            0.0 and 1.0.
	 */
	public void setMinProbability(double minProbability) {
		this.minProbability = minProbability;
	}
}