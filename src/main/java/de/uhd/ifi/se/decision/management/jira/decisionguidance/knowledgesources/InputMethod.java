package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources;

import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;

import java.util.List;

/**
 * The input method calculates the result based on a given Input
 * @param <T> Data that the results based on
 * @param <E> Type of the knowledge source
 */
public interface InputMethod<T, E extends KnowledgeSource> {

	/**
	 *
	 * @param input
	 * @return a list of recommendations
	 */
	List<Recommendation> getResults(T input);

	/**
	 * sets the configured knowledge source that contains all neccessary data to calculate a recommendation
	 * @param knowledgeSource
	 */
	void setData(E knowledgeSource);

}
