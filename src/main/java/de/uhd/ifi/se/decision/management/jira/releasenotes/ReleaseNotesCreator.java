package de.uhd.ifi.se.decision.management.jira.releasenotes;

import java.util.ArrayList;
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
	private List<ReleaseNotesEntry> proposals;
	private final ReleaseNotesConfiguration config;

	public ReleaseNotesCreator(List<Issue> jiraIssuesMatchingQuery, ReleaseNotesConfiguration releaseNoteConfiguration,
			ApplicationUser user) {
		this.config = releaseNoteConfiguration;
		proposals = jiraIssuesMatchingQuery.stream().map(jiraIssue -> {
			ReleaseNotesEntry entry = new ReleaseNotesEntry(jiraIssue, user);
			ReleaseNotesCategory category = config.decideCategory(jiraIssue);
			entry.setCategory(category);
			return entry;
		}).collect(Collectors.toList());
	}

	public ReleaseNotes proposeElements() {
		compareProposals(proposals);
		return proposeReleaseNotes(proposals);
	}

	/**
	 * compare all ReleaseNoteIssueProposal elements and set the rating for each
	 * category considering UserInput compare each element with the others of the
	 * same category scaling algorithm:
	 * https://stackoverflow.com/questions/5294955/how-to-scale-down-a-range-of-numbers-with-a-known-min-and-max-value
	 * alternative algorithm could be gaussian standard distribution other
	 * alternative could be median-interval-separation
	 * 
	 * @param proposals2
	 */
	private void compareProposals(List<ReleaseNotesEntry> proposals) {
		List<JiraIssueMetric> criteriaEnumList = List.of(JiraIssueMetric.values());

		// find median
		EnumMap<JiraIssueMetric, Integer> medianOfProposals = RatingCalculator.getMedianOfProposals(proposals);

		// for each criteria create a list of integers, so we can then compute min, max
		// values and the scales
		EnumMap<JiraIssueMetric, List<Integer>> countValues = RatingCalculator.getFlatListOfValues(proposals);

		// we later check in which interval the proposal would be and apply the
		// corresponding lower and higher value

		// add min and max to lists
		// the first value of the ArrayList is for the first interval and the second is
		// for the second interval
		EnumMap<JiraIssueMetric, List<Integer>> minValues = new EnumMap<>(JiraIssueMetric.class);
		EnumMap<JiraIssueMetric, List<Integer>> maxValues = new EnumMap<>(JiraIssueMetric.class);
		RatingCalculator.getMinAndMaxValues(minValues, maxValues, countValues, medianOfProposals);

		proposals.forEach(dkElement -> {
			EnumMap<JiraIssueMetric, Double> existingCriteriaValues = dkElement.getJiraIssueMetrics();
			// use ref object due to atomic problem etc.
			var totalRef = new Object() {
				Double total = 0.0;
			};
			criteriaEnumList.forEach(criteria -> {
				double scaling = 0;

				// check if criteria is in first or second interval
				int index = 0;
				int minVal = 1;
				int maxVal = 5;
				if (existingCriteriaValues.get(criteria) > medianOfProposals.get(criteria)
						&& minValues.get(criteria).size() > 1 && maxValues.get(criteria).size() > 1) {
					index = 1;
					minVal = 6;
					maxVal = 10;
				}
				// extra treatment for priorities, as there are no outliers and numbers are
				// reversed
				// we just do the scaling on all using min and max values
				if (criteria == JiraIssueMetric.PRIORITY) {
					int indexMinPrio = 0;
					int indexMaxPrio = 0;
					if (maxValues.get(criteria).size() > 1) {
						indexMaxPrio = 1;
					}
					scaling = RatingCalculator.scaleFromSmallToLarge(existingCriteriaValues.get(criteria),
							minValues.get(criteria).get(indexMinPrio), maxValues.get(criteria).get(indexMaxPrio), 1,
							10);
					scaling = 11 - scaling;
				} else {
					scaling = RatingCalculator.scaleFromSmallToLarge(existingCriteriaValues.get(criteria),
							minValues.get(criteria).get(index), maxValues.get(criteria).get(index), minVal, maxVal);
				}
				// multiply scaling with associated weighting input from user
				Double userWeight = config.getJiraIssueMetricWeights().get(criteria);
				if (userWeight != null) {
					scaling *= userWeight;
				} else {
					scaling *= 0;
				}
				totalRef.total += scaling;
			});
			// set rating
			dkElement.setRating(Math.round(totalRef.total));
		});
	}

	private ReleaseNotes proposeReleaseNotes(List<ReleaseNotesEntry> proposals) {
		List<ReleaseNotesEntry> bugs = new ArrayList<>();
		if (config.getAdditionalConfigurations().contains(AdditionalConfigurationOptions.INCLUDE_BUG_FIXES)) {
			bugs = filterEntriesByCategory(proposals, ReleaseNotesCategory.BUG_FIXES);
		}
		List<ReleaseNotesEntry> features = filterEntriesByCategory(proposals, ReleaseNotesCategory.NEW_FEATURES);
		List<ReleaseNotesEntry> improvements = filterEntriesByCategory(proposals, ReleaseNotesCategory.IMPROVEMENTS);

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
		entries = entries.stream().filter(entry -> entry.getCategory() == category).collect(Collectors.toList());
		Collections.sort(entries);
		return entries;
	}

}
