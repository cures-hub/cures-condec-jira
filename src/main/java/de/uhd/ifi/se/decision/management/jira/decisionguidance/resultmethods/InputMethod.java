package de.uhd.ifi.se.decision.management.jira.decisionguidance.resultmethods;

import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;

import java.util.List;

public interface InputMethod<T> {

	List<Recommendation> getResults(T input);

}
