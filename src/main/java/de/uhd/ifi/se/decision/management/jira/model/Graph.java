package de.uhd.ifi.se.decision.management.jira.model;

import java.util.List;
import java.util.Map;

/**
 * Interface for a graph of decision knowledge elements. The graph is directed,
 * i.e., links are arrows between a source and a destination element.
 */
public interface Graph {

	/**
	 * Get the root element of the graph, i.e., the element that the graph is
	 * created from.
	 *
	 * @see DecisionKnowledgeElement
	 * @return first decision knowledge element in the graph.
	 */
	DecisionKnowledgeElement getRootElement();

	/**
	 * Set the root element of the graph, i.e., the element that the graph is
	 * created from.
	 *
	 * @see DecisionKnowledgeElement
	 * @param rootElement
	 *            first decision knowledge element in the graph.
	 */
	void setRootElement(DecisionKnowledgeElement rootElement);

	/**
	 * Get the decision knowledge elements and their respective links that can be
	 * reached from the root element.
	 *
	 * @see DecisionKnowledgeElement
	 * @see Link
	 * @param element decision knowledge element in the graph
	 * @return map of decision knowledge elements and the respective links in the
	 *         graph.
	 */
	Map<DecisionKnowledgeElement, Link> getLinkedElementsAndLinks(DecisionKnowledgeElement element);

	/**
	 * Get the decision knowledge elements that can be reached from the root
	 * element.
	 *
	 * @see DecisionKnowledgeElement
	 * @param element decision knowledge element in the graph
	 * @return list of decision knowledge elements in the graph.
	 */
	List<DecisionKnowledgeElement> getLinkedElements(DecisionKnowledgeElement element);
}
