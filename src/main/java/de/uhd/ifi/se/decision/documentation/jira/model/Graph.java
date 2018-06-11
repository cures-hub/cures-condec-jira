package de.uhd.ifi.se.decision.documentation.jira.model;

import java.util.List;

/**
 * @description Interface for a graph of decision knowledge elements
 */
public interface Graph {
	public DecisionKnowledgeElement getRootElement();

	public void setRootElement(DecisionKnowledgeElement rootElement);

	public List<DecisionKnowledgeElement> getLinkedElements(DecisionKnowledgeElement element);
}
