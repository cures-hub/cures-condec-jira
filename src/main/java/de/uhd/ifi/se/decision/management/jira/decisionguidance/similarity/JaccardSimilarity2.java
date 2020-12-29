package de.uhd.ifi.se.decision.management.jira.decisionguidance.similarity;

import org.apache.commons.text.similarity.JaccardSimilarity;

public class JaccardSimilarity2 implements TextualSimilarity {

	@Override
	public double calculateSimilarity(String left, String right) {
		JaccardSimilarity jaccardSimilarity = new JaccardSimilarity();
		double jc = jaccardSimilarity.apply(left.toLowerCase(), right.toLowerCase());
		return jc;
	}
}
