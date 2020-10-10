package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.calculationmethods;

import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;

import java.util.List;

public interface CalculationMethod {

	List<Recommendation> getResults(String inputs);
}
