package de.uhd.ifi.se.decision.management.jira.quality.consistency.contextinformation;

import java.util.ArrayList;
import java.util.List;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.quality.consistency.suggestions.LinkSuggestion;

/**
 * Superclass for different context information providers to realize context
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
public abstract class ContextInformationProvider {

	protected String id;
	protected List<LinkSuggestion> linkSuggestions;

	public ContextInformationProvider() {
		this.linkSuggestions = new ArrayList<>();
	}

	/**
	 * @return id of the context information provider
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return name of the context information provider
	 */
	public String getName() {
		return this.getClass().getName();
	}

	public List<LinkSuggestion> getLinkSuggestions() {
		return linkSuggestions;
	}

	/**
	 * Calculates the relationship between the issues i1 and i2. Higher values
	 * indicate a higher similarity. The value is called Context Relationship
	 * Indicator in the paper.
	 *
	 * @param baseElement
	 * @param knowledgeElements
	 * @return value of relationship in [0, inf]
	 */
	public void assessRelations(KnowledgeElement baseElement, List<KnowledgeElement> knowledgeElements) {
		for (KnowledgeElement elementToTest : knowledgeElements) {
			assessRelation(baseElement, elementToTest);
		}
	}

	public abstract double assessRelation(KnowledgeElement baseElement, KnowledgeElement knowledgeElement);

}
