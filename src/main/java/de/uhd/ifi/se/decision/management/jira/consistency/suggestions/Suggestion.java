package de.uhd.ifi.se.decision.management.jira.consistency.suggestions;

public interface Suggestion<T> {

	/**
	 *
	 * @return suggestion of type T
	 */
	T getSuggestion();

}
