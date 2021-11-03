package de.uhd.ifi.se.decision.management.jira.releasenotes;

import java.util.EnumMap;

public enum JiraIssueMetric {
	COUNT_DECISION_KNOWLEDGE, PRIORITY, COUNT_COMMENTS, SIZE_SUMMARY, SIZE_DESCRIPTION, DAYS_COMPLETION, EXPERIENCE_RESOLVER, EXPERIENCE_REPORTER;

	@Override
	public String toString() {
		return name().toLowerCase();
	}

	/**
	 * @return map of all metrics with double default 1.0.
	 */
	public static EnumMap<JiraIssueMetric, Double> toEnumMap() {
		EnumMap<JiraIssueMetric, Double> criteriaTypes = new EnumMap<>(JiraIssueMetric.class);
		for (JiraIssueMetric criteriaType : JiraIssueMetric.values()) {
			criteriaTypes.put(criteriaType, 1.0);
		}
		return criteriaTypes;
	}
}