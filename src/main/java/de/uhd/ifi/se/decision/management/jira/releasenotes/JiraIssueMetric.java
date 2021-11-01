package de.uhd.ifi.se.decision.management.jira.releasenotes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;

public enum JiraIssueMetric {
	COUNT_DECISION_KNOWLEDGE, PRIORITY, COUNT_COMMENTS, SIZE_SUMMARY, SIZE_DESCRIPTION, DAYS_COMPLETION, EXPERIENCE_RESOLVER, EXPERIENCE_REPORTER;

	@Override
	public String toString() {
		return name().toLowerCase();
	}

	public static JiraIssueMetric getJiraIssueMetric(String type) {
		if (type == null) {
			return JiraIssueMetric.COUNT_DECISION_KNOWLEDGE;
		}
		switch (type.toLowerCase(Locale.ENGLISH)) {
		case "count_decision_knowledge":
			return JiraIssueMetric.COUNT_DECISION_KNOWLEDGE;
		case "priority":
			return JiraIssueMetric.PRIORITY;
		case "count_comments":
			return JiraIssueMetric.COUNT_COMMENTS;
		case "size_summary":
			return JiraIssueMetric.SIZE_SUMMARY;
		case "size_description":
			return JiraIssueMetric.SIZE_DESCRIPTION;
		case "days_completion":
			return JiraIssueMetric.DAYS_COMPLETION;
		case "experience_resolver":
			return JiraIssueMetric.EXPERIENCE_RESOLVER;
		case "experience_reporter":
			return JiraIssueMetric.EXPERIENCE_REPORTER;
		default:
			return JiraIssueMetric.COUNT_DECISION_KNOWLEDGE;
		}
	}

	/**
	 * @return hashMap of metrics with integer default 0.
	 */
	public static EnumMap<JiraIssueMetric, Integer> toIntegerEnumMap() {
		EnumMap<JiraIssueMetric, Integer> criteriaTypes = new EnumMap<>(JiraIssueMetric.class);
		for (JiraIssueMetric criteriaType : JiraIssueMetric.values()) {
			criteriaTypes.put(criteriaType, 0);
		}
		return criteriaTypes;
	}

	/**
	 * @return hashMap of metrics with double default 1.0
	 */
	public static EnumMap<JiraIssueMetric, Double> toDoubleEnumMap() {
		EnumMap<JiraIssueMetric, Double> criteriaTypes = new EnumMap<>(JiraIssueMetric.class);
		for (JiraIssueMetric criteriaType : JiraIssueMetric.values()) {
			criteriaTypes.put(criteriaType, 1.0);
		}
		return criteriaTypes;
	}

	/**
	 * @return list of all metrics as Strings.
	 */
	public static List<String> toList() {
		List<String> criteriaTypes = new ArrayList<>();
		for (JiraIssueMetric criteriaType : JiraIssueMetric.values()) {
			criteriaTypes.add(criteriaType.toString());
		}
		return criteriaTypes;
	}

	/**
	 * @return list of metrics.
	 */
	public static List<JiraIssueMetric> getOriginalList() {
		return new ArrayList<>(Arrays.asList(JiraIssueMetric.values()));
	}

}
