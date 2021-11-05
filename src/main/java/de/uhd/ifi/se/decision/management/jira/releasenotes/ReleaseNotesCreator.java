package de.uhd.ifi.se.decision.management.jira.releasenotes;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.stream.Collectors;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.user.ApplicationUser;

/**
 * Computes the metrics for the proposals and to compare the ratings.
 */
public class ReleaseNotesCreator {
	private List<ReleaseNotesEntry> releaseNoteEntries;

	public ReleaseNotesCreator(List<Issue> jiraIssuesMatchingQuery, ReleaseNotesConfiguration releaseNotesConfiguration,
			ApplicationUser user) {
		releaseNoteEntries = jiraIssuesMatchingQuery.stream().map(jiraIssue -> {
			ReleaseNotesEntry entry = new ReleaseNotesEntry(jiraIssue, user);
			ReleaseNotesCategory category = releaseNotesConfiguration.decideCategory(jiraIssue);
			entry.setCategory(category);
			double rating = calculateRating(entry.getJiraIssueMetrics(),
					releaseNotesConfiguration.getJiraIssueMetricWeights());
			entry.setRating(rating);
			return entry;
		}).collect(Collectors.toList());
	}

	public double calculateRating(EnumMap<JiraIssueMetric, Double> jiraIssueMetrics,
			EnumMap<JiraIssueMetric, Double> jiraIssueMetricWeights) {
		double rating = 0;
		for (JiraIssueMetric metric : jiraIssueMetricWeights.keySet()) {
			rating += jiraIssueMetrics.getOrDefault(metric, 0.0) * jiraIssueMetricWeights.getOrDefault(metric, 0.0);
		}
		return rating;
	}

	/**
	 * @return {@link ReleaseNotes} with proposals for {@link ReleaseNotesEntry
	 *         release notes entries}. The content in markdown syntax is not set
	 *         yet.
	 */
	public ReleaseNotes proposeReleaseNotes() {
		List<ReleaseNotesEntry> bugs = filterEntriesByCategory(releaseNoteEntries, ReleaseNotesCategory.BUG_FIXES);
		List<ReleaseNotesEntry> features = filterEntriesByCategory(releaseNoteEntries,
				ReleaseNotesCategory.NEW_FEATURES);
		List<ReleaseNotesEntry> improvements = filterEntriesByCategory(releaseNoteEntries,
				ReleaseNotesCategory.IMPROVEMENTS);

		if (improvements.isEmpty() && features.isEmpty() && bugs.isEmpty()) {
			return null;
		}
		ReleaseNotes releaseNotes = new ReleaseNotes();
		releaseNotes.setBugFixes(bugs);
		releaseNotes.setImprovements(improvements);
		releaseNotes.setNewFeatures(features);
		return releaseNotes;
	}

	public List<ReleaseNotesEntry> filterEntriesByCategory(List<ReleaseNotesEntry> entries,
			ReleaseNotesCategory category) {
		List<ReleaseNotesEntry> filteredEntries = entries.stream().filter(entry -> entry.getCategory() == category)
				.collect(Collectors.toList());
		Collections.sort(filteredEntries);
		return filteredEntries;
	}
}