package de.uhd.ifi.se.decision.management.jira.filtering;

import java.util.List;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;

/**
 * Interface for accessing the filtered knowledge graphs. The filter criteria
 * are specified in the {@link FilterSettings} class.
 * 
 * @see FilterSettings
 * @see KnowledgeGraph
 */
public interface FilterExtractor {

	/**
	 * Returns a list of all trees that match the given filter criteria.
	 */
	List<List<DecisionKnowledgeElement>> getAllGraphs();

	// Problem Filtered Issues from sideFilter will be filterd again
	// In the end there are only 2 Issues left that are not matching with the
	// location so everything is collapsed
	List<DecisionKnowledgeElement> getAllElementsMatchingQuery();

	List<DecisionKnowledgeElement> getAllElementsMatchingCompareFilter();

	FilterSettings getFilterSettings();

	List<DecisionKnowledgeElement> getElementsLinkTypeFilterMatches(List<DecisionKnowledgeElement> allDecisions);

	ApplicationUser getUser();
}