package de.uhd.ifi.se.decision.management.jira.decisionguidance.recommender;

import java.util.Arrays;
import java.util.List;

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

	public static List<RecommenderType> getRecommenderTypes() {
		return Arrays.asList(RecommenderType.values());
	}


}
