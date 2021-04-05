package de.uhd.ifi.se.decision.management.jira.decisionguidance.recommender;

import java.util.Arrays;
import java.util.List;

/**
 * Type of the input, either keywords or decision problem (issue) with linked
 * elements.
 */
public enum RecommenderType {
	KEYWORD, ISSUE;

	/**
	 * @return default (i.e. simple) recommender type.
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