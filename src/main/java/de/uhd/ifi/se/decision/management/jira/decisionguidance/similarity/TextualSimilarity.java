package de.uhd.ifi.se.decision.management.jira.decisionguidance.similarity;

public interface TextualSimilarity {

	double calculateSimilarity(String left, String right);
}
