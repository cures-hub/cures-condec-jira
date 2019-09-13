package de.uhd.ifi.se.decision.management.jira.releasenotes;

import java.util.*;


public enum IssueMetric {
	COUNT_DECISION_KNOWLEDGE, PRIORITY, COUNT_COMMENTS, SIZE_SUMMARY, SIZE_DESCRIPTION, DAYS_COMPLETION, EXPERIENCE_RESOLVER, EXPERIENCE_REPORTER;


	@Override
	public String toString() {
		return this.name().toLowerCase(Locale.ENGLISH);
	}


	public static IssueMetric getIssueMetric(String type) {
		if (type == null) {
			return IssueMetric.COUNT_DECISION_KNOWLEDGE;
		}
		switch (type.toLowerCase(Locale.ENGLISH)) {
			case "count_decision_knowledge":
				return IssueMetric.COUNT_DECISION_KNOWLEDGE;
			case "priority":
				return IssueMetric.PRIORITY;
			case "count_comments":
				return IssueMetric.COUNT_COMMENTS;
			case "size_summary":
				return IssueMetric.SIZE_SUMMARY;
			case "size_description":
				return IssueMetric.SIZE_DESCRIPTION;
			case "days_completion":
				return IssueMetric.DAYS_COMPLETION;
			case "experience_resolver":
				return IssueMetric.EXPERIENCE_RESOLVER;
			case "experience_reporter":
				return IssueMetric.EXPERIENCE_REPORTER;
			default:
				return IssueMetric.COUNT_DECISION_KNOWLEDGE;
		}
	}

	/**
	 * @return hashMap of metrics with integer default 0.
	 */
	public static EnumMap<IssueMetric, Integer> toIntegerEnumMap() {
		EnumMap<IssueMetric, Integer> criteriaTypes = new EnumMap<>(IssueMetric.class);
		for (IssueMetric criteriaType : IssueMetric.values()) {
			criteriaTypes.put(criteriaType, 0);
		}
		return criteriaTypes;
	}

	/**
	 * @return hashMap of metrics with double default 1.0
	 */
	public static EnumMap<IssueMetric, Double> toDoubleEnumMap() {
		EnumMap<IssueMetric, Double> criteriaTypes = new EnumMap<>(IssueMetric.class);
		for (IssueMetric criteriaType : IssueMetric.values()) {
			criteriaTypes.put(criteriaType, 1.0);
		}
		return criteriaTypes;
	}

	/**
	 * Convert all metrics to strings.
	 *
	 * @return list of metrics  as Strings.
	 */
	public static List<String> toList() {
		List<String> criteriaTypes = new ArrayList<String>();
		for (IssueMetric criteriaType : IssueMetric.values()) {
			criteriaTypes.add(criteriaType.toString());
		}
		return criteriaTypes;
	}

	/**
	 * @return list of metrics.
	 */
	public static List<IssueMetric> getOriginalList() {
		return new ArrayList<IssueMetric>(Arrays.asList(IssueMetric.values()));
	}

}
