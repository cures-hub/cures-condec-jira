package de.uhd.ifi.se.decision.management.jira.decisionguidance.similarity;

import org.apache.commons.text.similarity.LevenshteinDistance;

public class LevenstheinDistance2 implements TextualSimilarity {

	@Override
	public double calculateSimilarity(String left, String right) {
		LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
		double jw = levenshteinDistance.apply(left.toLowerCase(), right.toLowerCase());
		return jw;
	}
}
