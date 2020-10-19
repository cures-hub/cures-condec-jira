package de.uhd.ifi.se.decision.management.jira.decisionguidance.recommender;

public enum RecommenderType {
	KEYWORD, ISSUE;

	/**
	 * Change the default value here
	 * @return
	 */
	public static String getDefault() {
		return KEYWORD.toString();
	}
}
