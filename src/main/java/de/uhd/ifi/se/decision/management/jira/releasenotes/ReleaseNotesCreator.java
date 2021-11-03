package de.uhd.ifi.se.decision.management.jira.releasenotes;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.user.ApplicationUser;

/**
 * Class to compute the metrics for the proposals and to compare the ratings.
 */
public class ReleaseNotesCreator {
	private List<ReleaseNotesEntry> proposals;
	private final ReleaseNotesConfiguration config;

	public ReleaseNotesCreator(List<Issue> jiraIssuesMatchingQuery, ReleaseNotesConfiguration releaseNoteConfiguration,
			ApplicationUser user) {
		this.config = releaseNoteConfiguration;
		this.proposals = jiraIssuesMatchingQuery.stream()
				.map(jiraIssue -> new ReleaseNotesEntry(jiraIssue, user)).collect(Collectors.toList());
	}

	public Map<String, List<ReleaseNotesEntry>> getMappedProposals() {
		compareProposals(proposals);
		return mapProposals(proposals);
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

	private Map<String, List<ReleaseNotesEntry>> mapProposals(
			List<ReleaseNotesEntry> proposals) {
		IssueManager issueManager = ComponentAccessor.getIssueManager();

		Map<String, List<ReleaseNotesEntry>> resultMap = new HashMap<>();
		List<ReleaseNotesEntry> bugs = new ArrayList<>();
		List<ReleaseNotesEntry> features = new ArrayList<>();
		List<ReleaseNotesEntry> improvements = new ArrayList<>();
		var ref = new Object() {
			Boolean hasResult = false;
		};
		proposals.forEach(proposal -> {
			Issue issue = issueManager.getIssueByCurrentKey(proposal.getElement().getKey());
			IssueType issueType = issue.getIssueType();
			Integer issueTypeId = Integer.valueOf(issueType.getId());
			// new features
			if (config.getFeatureMapping() != null && config.getFeatureMapping().contains(issueTypeId)) {
				features.add(proposal);
				ref.hasResult = true;
			}
			// bugs
			// check if include bugs is false
			if (config.getBugFixMapping() != null && config.getBugFixMapping().contains(issueTypeId)
					&& config.getAdditionalConfiguration().get(AdditionalConfigurationOptions.INCLUDE_BUG_FIXES)) {
				bugs.add(proposal);
				ref.hasResult = true;
			}
			// improvements
			if (config.getImprovementMapping() != null && config.getImprovementMapping().contains(issueTypeId)) {
				improvements.add(proposal);
				ref.hasResult = true;
			}

		});
		if (!ref.hasResult) {
			return null;
		}
		Comparator<ReleaseNotesEntry> compareByRating = new Comparator<>() {
			@Override
			public int compare(ReleaseNotesEntry o1, ReleaseNotesEntry o2) {
				Double rating1 = o1.getRating();
				Double rating2 = o2.getRating();
				return rating2.compareTo(rating1);
			}
		};
		bugs.sort(compareByRating);
		features.sort(compareByRating);
		improvements.sort(compareByRating);

		resultMap.put(ReleaseNotesCategory.BUG_FIXES.toString(), bugs);
		resultMap.put(ReleaseNotesCategory.NEW_FEATURES.toString(), features);
		resultMap.put(ReleaseNotesCategory.IMPROVEMENTS.toString(), improvements);
		return resultMap;
	}

}
