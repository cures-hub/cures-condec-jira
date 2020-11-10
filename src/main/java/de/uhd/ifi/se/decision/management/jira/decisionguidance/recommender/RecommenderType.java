package de.uhd.ifi.se.decision.management.jira.decisionguidance.recommender;

public enum RecommenderType {
	KEYWORD, ISSUE, EVALUATION;

	/**
	 * Change the default value here
	 *
	 * @return
	 */
	public static RecommenderType getDefault() {
		return KEYWORD;
	}

	public static RecommenderType getTypeByString(String value) {
		try {
			return valueOf(value);
		} catch (IllegalArgumentException e) {
			return KEYWORD;
		}
	}


}
