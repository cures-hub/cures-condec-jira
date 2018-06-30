package de.uhd.ifi.se.decision.documentation.jira.model;

import java.util.List;
import java.util.Map;

/**
 * Interface for a graph of decision knowledge elements
 */
public interface Graph {
	DecisionKnowledgeElement getRootElement();

	void setRootElement(DecisionKnowledgeElement rootElement);

	List<DecisionKnowledgeElement> getLinkedElements(DecisionKnowledgeElement element);

	Map<DecisionKnowledgeElement, Link> getLinkedElementsAndLinks(DecisionKnowledgeElement element);
}
