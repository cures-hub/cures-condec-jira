package de.uhd.ifi.se.decision.management.jira.recommendation;

import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.suggestions.SuggestionType;

public interface Recommendation extends Comparable<Recommendation> {

	/**
	 *
	 * @return suggestion type of suggestion
	 */
	SuggestionType getSuggestionType();

	public RecommendationScore getScore();

	default public int compareTo(Recommendation o) {
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
