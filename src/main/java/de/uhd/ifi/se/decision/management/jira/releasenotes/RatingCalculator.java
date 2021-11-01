package de.uhd.ifi.se.decision.management.jira.releasenotes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;

/**
 * Private class to calculate the ratings for relevance.
 */
class RatingCalculator {

	/**
	 * Scale a number from x to y
	 *
	 * @param valueIn
	 * @param baseMin
	 * @param baseMax
	 * @return double
	 */
	protected static double scaleFromSmallToLarge(final double valueIn, final double baseMin, final double baseMax,
			double limitMin, double limitMax) {
		// check if baseMax===base min
		if (baseMax - baseMin == 0) {
			return limitMax;
		}
		return ((limitMax - limitMin) * (valueIn - baseMin) / (baseMax - baseMin)) + limitMin;
	}

	/**
	 *
	 * @param proposals
	 * @return
	 */
	protected static EnumMap<JiraIssueMetric, ArrayList<Integer>> getFlatListOfValues(
			List<ReleaseNotesIssueProposal> proposals) {

		EnumMap<JiraIssueMetric, ArrayList<Integer>> countValues = new EnumMap<>(JiraIssueMetric.class);
		List<JiraIssueMetric> criteriaEnumList = JiraIssueMetric.getOriginalList();

		proposals.forEach(dkElement -> {
			EnumMap<JiraIssueMetric, Integer> existingCriteriaValues = dkElement.getMetrics();
			// add values to
			criteriaEnumList.forEach(criteria -> {

				Integer currentValue = existingCriteriaValues.get(criteria);

				ArrayList<Integer> existingValues = countValues.get(criteria);

				if (existingValues == null) {
					// init new list
					ArrayList<Integer> newList = new ArrayList<>();
					// add value to new list
					newList.add(currentValue);
					countValues.put(criteria, newList);
				} else {
					existingValues.add(currentValue);
				}
			});
		});
		return countValues;
	}

	/**
	 *
	 * @param minValues
	 * @param maxValues
	 * @param countValues
	 * @param medianOfProposals
	 */
	protected static void getMinAndMaxValues(EnumMap<JiraIssueMetric, ArrayList<Integer>> minValues,
			EnumMap<JiraIssueMetric, ArrayList<Integer>> maxValues,
			EnumMap<JiraIssueMetric, ArrayList<Integer>> countValues,
			EnumMap<JiraIssueMetric, Integer> medianOfProposals) {
		List<JiraIssueMetric> criteriaEnumList = JiraIssueMetric.getOriginalList();
		criteriaEnumList.forEach(criteria -> {
			ArrayList<Integer> values = countValues.get(criteria);
			ArrayList<ArrayList<Integer>> valuesInInterval = new ArrayList<>();
			ArrayList<Integer> firstInterval = new ArrayList<>();
			ArrayList<Integer> secondInterval = new ArrayList<>();
			if (values != null && values.size() > 0) {
				// first interval
				values.forEach(value -> {
					if (value <= medianOfProposals.get(criteria)) {
						firstInterval.add(value);
					} else {
						// second interval
						secondInterval.add(value);
					}
				});
				valuesInInterval.add(firstInterval);
				valuesInInterval.add(secondInterval);
				ArrayList<Integer> mins = new ArrayList<>();
				ArrayList<Integer> maxs = new ArrayList<>();
				if (valuesInInterval.get(0).size() > 0) {
					mins.add(Collections.min(valuesInInterval.get(0)));
					maxs.add(Collections.max(valuesInInterval.get(0)));
				}
				if (valuesInInterval.get(1).size() > 0) {
					mins.add(Collections.min(valuesInInterval.get(1)));
					maxs.add(Collections.max(valuesInInterval.get(1)));
				}

				minValues.put(criteria, mins);
				maxValues.put(criteria, maxs);
			}
		});
	}

	protected static EnumMap<JiraIssueMetric, Integer> getMedianOfProposals(List<ReleaseNotesIssueProposal> proposals) {
		List<JiraIssueMetric> criteriaEnumList = JiraIssueMetric.getOriginalList();
		EnumMap<JiraIssueMetric, Integer> medians = new EnumMap<>(JiraIssueMetric.class);
		criteriaEnumList.forEach(criteria -> {
			ArrayList<Integer> flatList = new ArrayList<>();
			proposals.forEach(proposal -> {
				flatList.add(proposal.getMetrics().get(criteria));
			});
			// sort list
			Collections.sort(flatList);
			// find median
			double medianIndex = 0.0;
			if (flatList.size() > 0) {
				medianIndex = ((double) flatList.size()) / 2;
			}
			// use floor value
			medianIndex = Math.floor(medianIndex);
			// the median is the value at index medianIndex
			medians.put(criteria, (flatList.get((int) medianIndex)));
		});
		return medians;
	}

}
