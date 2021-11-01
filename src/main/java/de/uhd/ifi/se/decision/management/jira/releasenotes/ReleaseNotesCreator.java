package de.uhd.ifi.se.decision.management.jira.releasenotes;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;

/**
 * Class to compute the metrics for the proposals and to compare the ratings.
 */
public class ReleaseNotesCreator {
	private List<ReleaseNotesIssueProposal> proposals;
	private final List<Issue> elementsMatchingQuery;
	private final ReleaseNotesConfiguration config;
	private final ApplicationUser user;

	public ReleaseNotesCreator(List<Issue> jiraIssuesMatchingQuery, ReleaseNotesConfiguration releaseNoteConfiguration,
			ApplicationUser user) {
		this.elementsMatchingQuery = jiraIssuesMatchingQuery;
		this.config = releaseNoteConfiguration;
		this.user = user;
	}

	public Map<String, List<ReleaseNotesIssueProposal>> getMappedProposals() {
		setMetrics();
		compareProposals();
		return mapProposals();
	}

	/**
	 * Gather priority metrics for the Release Note Issue Proposal
	 * <p>
	 * sets Proposals
	 */
	private void setMetrics() {
		List<ReleaseNotesIssueProposal> releaseNoteIssueProposals = new ArrayList<>();
		// set up components we need to gather metrics
		IssueManager issueManager = ComponentAccessor.getIssueManager();

		// create plain array with no duplicates
		List<String> usedKeys = new ArrayList<>();
		Map<String, Integer> reporterIssueCount = new HashMap<>();
		Map<String, Integer> resolverIssueCount = new HashMap<>();
		// for each DecisionKnowledgeElement create one ReleaseNoteIssueProposal element
		// with the data
		HashMap<String, Integer> dkLinkedCount = new HashMap<String, Integer>();

		for (int i = 0; i < elementsMatchingQuery.size(); i++) {
			KnowledgeElement dkElement = new KnowledgeElement(elementsMatchingQuery.get(i));
			// add key to used keys
			usedKeys.add(dkElement.getKey());
			// create Release note issue proposal with the element and the count of
			// associated decision knowledge
			// check if DK or Comment
			ReleaseNotesIssueProposal proposal = new ReleaseNotesIssueProposal(dkElement, 0);
			String dkKey = dkElement.getKey();

			// check if it is a dk Issue or just a DK comment
			// comments are not rated, just counted
			if (dkKey.contains(":")) {
				String[] parts = dkKey.split(":");
				Integer currentCount = dkLinkedCount.get(parts[0]);
				if (currentCount != null) {
					currentCount += 1;
					dkLinkedCount.put(parts[0], currentCount);
				} else {
					dkLinkedCount.put(parts[0], 1);
				}
			} else {
				Issue issue = issueManager.getIssueByCurrentKey(dkElement.getKey());

				// set priority
				proposal.getAndSetPriority(issue);

				// set count of comments
				proposal.getAndSetCountOfComments(issue);

				// set size summary
				proposal.getAndSetSizeOfSummary();

				// set size description
				proposal.getAndSetSizeOfDescription();

				// set days to complete
				proposal.getAndSetDaysToCompletion(issue);

				// set experience reporter
				proposal.getAndSetExperienceReporter(issue, reporterIssueCount, user);

				// set experience resolver
				proposal.getAndSetExperienceResolver(issue, resolverIssueCount, user);

				// add to results
				releaseNoteIssueProposals.add(proposal);
			}
		}

		// now check DK element links
		for (Map.Entry<String, Integer> entry : dkLinkedCount.entrySet()) {
			String key = entry.getKey();
			Integer value = entry.getValue();
			releaseNoteIssueProposals.forEach(proposal -> {
				if (proposal.getDecisionKnowledgeElement().getKey().equals(key)) {
					proposal.getMetrics().put(JiraIssueMetric.COUNT_DECISION_KNOWLEDGE, value);
				}
			});
		}
		proposals = releaseNoteIssueProposals;
	}

	/**
	 * compare all ReleaseNoteIssueProposal elements and set the rating for each
	 * category considering UserInput compare each element with the others of the
	 * same category scaling algorithm:
	 * https://stackoverflow.com/questions/5294955/how-to-scale-down-a-range-of-numbers-with-a-known-min-and-max-value
	 * alternative algorithm could be gaussian standard distribution other
	 * alternative could be median-interval-separation
	 */
	private void compareProposals() {
		List<JiraIssueMetric> criteriaEnumList = JiraIssueMetric.getOriginalList();

		// find median
		EnumMap<JiraIssueMetric, Integer> medianOfProposals = RatingCalculator.getMedianOfProposals(proposals);

		// for each criteria create a list of integers, so we can then compute min, max
		// values and the scales
		EnumMap<JiraIssueMetric, ArrayList<Integer>> countValues = RatingCalculator.getFlatListOfValues(proposals);

		// we later check in which interval the proposal would be and apply the
		// corresponding lower and higher value

		// add min and max to lists
		// the first value of the ArrayList is for the first interval and the second is
		// for the second interval
		EnumMap<JiraIssueMetric, ArrayList<Integer>> minValues = new EnumMap<>(JiraIssueMetric.class);
		EnumMap<JiraIssueMetric, ArrayList<Integer>> maxValues = new EnumMap<>(JiraIssueMetric.class);
		RatingCalculator.getMinAndMaxValues(minValues, maxValues, countValues, medianOfProposals);

		proposals.forEach(dkElement -> {
			EnumMap<JiraIssueMetric, Integer> existingCriteriaValues = dkElement.getMetrics();
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
				Double userWeight = config.getJiraIssueMetricWeight().get(criteria);
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

	private Map<String, List<ReleaseNotesIssueProposal>> mapProposals() {
		IssueManager issueManager = ComponentAccessor.getIssueManager();

		Map<String, List<ReleaseNotesIssueProposal>> resultMap = new HashMap<>();
		List<ReleaseNotesIssueProposal> bugs = new ArrayList<>();
		List<ReleaseNotesIssueProposal> features = new ArrayList<>();
		List<ReleaseNotesIssueProposal> improvements = new ArrayList<>();
		var ref = new Object() {
			Boolean hasResult = false;
		};
		proposals.forEach(proposal -> {
			Issue issue = issueManager.getIssueByCurrentKey(proposal.getDecisionKnowledgeElement().getKey());
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
		Comparator<ReleaseNotesIssueProposal> compareByRating = new Comparator<>() {
			@Override
			public int compare(ReleaseNotesIssueProposal o1, ReleaseNotesIssueProposal o2) {
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
