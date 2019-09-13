package de.uhd.ifi.se.decision.management.jira.releasenotes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;

/**
 * Private class to do the calculations of the ratings
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
	static double scaleFromSmallToLarge(final double valueIn, final double baseMin, final double baseMax, double limitMin, double limitMax) {
		//check if baseMax===base min
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
	static EnumMap<IssueMetric, ArrayList<Integer>> getFlatListOfValues(ArrayList<ReleaseNoteIssueProposal> proposals) {

		EnumMap<IssueMetric, ArrayList<Integer>> countValues = new EnumMap<IssueMetric, ArrayList<Integer>>(IssueMetric.class);
		List<IssueMetric> criteriaEnumList = IssueMetric.getOriginalList();

		proposals.forEach(dkElement -> {
			EnumMap<IssueMetric, Integer> existingCriteriaValues = dkElement.getMetrics();
			//add values to
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
	static void getMinAndMaxValues(EnumMap<IssueMetric, ArrayList<Integer>> minValues, EnumMap<IssueMetric, ArrayList<Integer>> maxValues, EnumMap<IssueMetric, ArrayList<Integer>> countValues, EnumMap<IssueMetric, Integer> medianOfProposals) {
		List<IssueMetric> criteriaEnumList = IssueMetric.getOriginalList();
		criteriaEnumList.forEach(criteria -> {
			ArrayList<Integer> values = countValues.get(criteria);
			ArrayList<ArrayList<Integer>> valuesInInterval = new ArrayList<ArrayList<Integer>>();
			ArrayList<Integer> firstInterval = new ArrayList<Integer>();
			ArrayList<Integer> secondInterval = new ArrayList<Integer>();
			if (values != null && values.size() > 0) {
				//first interval
				values.forEach(value -> {
					if (value <= medianOfProposals.get(criteria)) {
						firstInterval.add(value);
					} else {
						//second interval
						secondInterval.add(value);
					}
				});
				valuesInInterval.add(firstInterval);
				valuesInInterval.add(secondInterval);
				ArrayList<Integer> mins = new ArrayList<Integer>();
				ArrayList<Integer> maxs = new ArrayList<Integer>();
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


	static EnumMap<IssueMetric, Integer> getMedianOfProposals(ArrayList<ReleaseNoteIssueProposal> proposals) {
		List<IssueMetric> criteriaEnumList = IssueMetric.getOriginalList();
		EnumMap<IssueMetric, Integer> medians = new EnumMap<IssueMetric, Integer>(IssueMetric.class);
		criteriaEnumList.forEach(criteria -> {
			ArrayList<Integer> flatList = new ArrayList<Integer>();
			proposals.forEach(proposal -> {
				flatList.add(proposal.getMetrics().get(criteria));
			});
			//sort list
			Collections.sort(flatList);
			//find median
			double medianIndex = 0.0;
			if (flatList.size() > 0) {
				medianIndex = ((double) flatList.size()) / 2;
			}
			//use floor value
			medianIndex = Math.floor(medianIndex);
			//the median is the value at index medianIndex
			medians.put(criteria, (flatList.get((int) medianIndex)));
		});
		return medians;
	}

}
