package de.uhd.ifi.se.decision.management.jira.releasenotes;

import java.util.EnumMap;

/**
 * Metrics that characterize a single Jira issue. For example, the number of
 * linked decision knowledge elements is one criterion to characterize a Jira
 * issue. Used for the ranking of Jira issues in release notes.
 */
public enum JiraIssueMetric {
	DECISION_KNOWLEDGE_COUNT(3.0), // number of decision knowledge elements linked to the Jira issue
	PRIORITY(1.0), // e.g. low, medium, high
	COMMENT_COUNT(1.0), // number of comments of the Jira issue
	SIZE_SUMMARY(0.1), // length of summary text
	SIZE_DESCRIPTION(0.1), // length of description text
	DAYS_COMPLETION(0.1), // number of days that the Jira issue was open
	EXPERIENCE_RESOLVER(0.1), // number of resolved Jira issues by the assignee user
	EXPERIENCE_REPORTER(0.1); // number of reported Jira issues by the reporter user

	private double defaultWeight;

	private JiraIssueMetric(double defaultWeight) {
		this.defaultWeight = defaultWeight;
	}

	public double getDefaultWeight() {
		return defaultWeight;
	}

	/**
	 * @return map of all metrics with default value 1.0. The map is both used to
	 *         store the metrics values as well as their weights when calculating
	 *         the rating of a {@link ReleaseNotesEntry}.
	 */
	public static EnumMap<JiraIssueMetric, Double> toEnumMap() {
		EnumMap<JiraIssueMetric, Double> jiraIssueMetrics = new EnumMap<>(JiraIssueMetric.class);
		for (JiraIssueMetric metric : JiraIssueMetric.values()) {
			jiraIssueMetrics.put(metric, metric.defaultWeight);
		}
		return jiraIssueMetrics;
	}

	@Override
	public String toString() {
		return name().toLowerCase();
	}
}