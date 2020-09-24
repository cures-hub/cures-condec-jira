package de.uhd.ifi.se.decision.management.jira.decisionguidance.scorecalculator;

import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;

public interface ScoreCalculator<T extends Recommendation> {
	int calculateScore(T recommendation);
}
