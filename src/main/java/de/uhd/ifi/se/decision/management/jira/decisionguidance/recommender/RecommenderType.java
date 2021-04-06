package de.uhd.ifi.se.decision.management.jira.decisionguidance.recommender;

import java.util.Arrays;
import java.util.List;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;

/**
 * Type of the input, either keywords or decision problem (issue) with linked
 * elements. Can also be combined (i.e. both input types are used).
 */
public enum RecommenderType {
	KEYWORD, ISSUE, COMBINED;

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

	public static RecommenderType determineType(KnowledgeElement decisionProblem, String keywords) {
		if (decisionProblem == null) {
			return KEYWORD;
		} else if (keywords == null || keywords.isBlank()) {
			return ISSUE;
		}
		return COMBINED;
	}
}