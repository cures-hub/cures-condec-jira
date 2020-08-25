package de.uhd.ifi.se.decision.management.jira.quality.consistency.contextinformation;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.quality.consistency.suggestions.LinkSuggestion;

import java.util.Collection;
import java.util.List;

/**
 * Interface for different context information provider.
 * Context utility functions are realized by so called context information providers.
 * The currently available context information providers, each representing a different
 * kind of context information used for calculating the architectural knowledge context.
 * This interface is part of the Decorator pattern.
 *
 * @author Philipp de Sombre
 * @implSpec C. Miesbauer and R. Weinreich,
 * "Capturing and Maintaining Architectural Knowledge Using Context Information",
 * 2012 Joint Working IEEE/IFIP Conference on Software Architecture and European Conference on Software Architecture
 */
public interface ContextInformationProvider {

	/**
	 * @return id of the context information provider
	 */
	String getId();

	/**
	 * @return name of the context information provider
	 */
	String getName();


	Collection<LinkSuggestion> getLinkSuggestions();


	/**
	 * Calculates the relationship between the issues i1 and i2. Higher values indicate a higher similarity.
	 * The value is called Context Relationship Indicator in the paper.
	 *
	 * @param baseElement
	 * @param knowledgeElements
	 * @return value of relationship in [0, inf]
	 */
	void assessRelation(KnowledgeElement baseElement, List<KnowledgeElement> knowledgeElements);

}
