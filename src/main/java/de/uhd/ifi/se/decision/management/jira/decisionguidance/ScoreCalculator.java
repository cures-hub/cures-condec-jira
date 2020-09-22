package de.uhd.ifi.se.decision.management.jira.decisionguidance;

import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;

public interface ScoreCalculator<T extends Recommendation> {
	int calculateScore(T recommendation);
}
