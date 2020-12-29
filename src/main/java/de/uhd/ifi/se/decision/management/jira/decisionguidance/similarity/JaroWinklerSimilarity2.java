package de.uhd.ifi.se.decision.management.jira.decisionguidance.similarity;

import org.apache.commons.text.similarity.JaccardSimilarity;
import org.apache.commons.text.similarity.JaroWinklerDistance;

public class JaroWinklerSimilarity2 implements TextualSimilarity {

	@Override
	public double calculateSimilarity(String left, String right) {
		JaroWinklerDistance jaroWinklerDistance = new JaroWinklerDistance();
		double jw = jaroWinklerDistance.apply(left.toLowerCase(), right.toLowerCase());
		return jw;
	}
}
