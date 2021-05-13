package de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.suggestions;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;

// TODO Unify with Recommendation class
public interface Suggestion<T extends KnowledgeElement> {

	/**
	 *
	 * @return suggestion of type T
	 */
	T getSuggestion();

	/**
	 *
	 * @return suggestion type of suggestion
	 */
	SuggestionType getSuggestionType();

}
