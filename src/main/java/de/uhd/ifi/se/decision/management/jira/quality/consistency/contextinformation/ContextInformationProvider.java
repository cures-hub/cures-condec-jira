package de.uhd.ifi.se.decision.management.jira.quality.consistency.contextinformation;

import java.util.Collection;
import java.util.List;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.quality.consistency.suggestions.LinkSuggestion;

/**
 * Interface for different context information providers to realize context
 * utility functions. For example, the {@link TimeContextInformationProvider}
 * rates relations based on time of creation or modifications of elements.
 * 
 * This interface is part of the Decorator design pattern.
 *
 * @implSpec C. Miesbauer and R. Weinreich, "Capturing and Maintaining
 *           Architectural Knowledge Using Context Information", 2012 Joint
 *           Working IEEE/IFIP Conference on Software Architecture and European
 *           Conference on Software Architecture
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
	 * Calculates the relationship between the issues i1 and i2. Higher values
	 * indicate a higher similarity. The value is called Context Relationship
	 * Indicator in the paper.
	 *
	 * @param baseElement
	 * @param knowledgeElements
	 * @return value of relationship in [0, inf]
	 */
	void assessRelation(KnowledgeElement baseElement, List<KnowledgeElement> knowledgeElements);

}
