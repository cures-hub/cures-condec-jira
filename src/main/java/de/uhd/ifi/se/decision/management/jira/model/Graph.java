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
	 * Get the adjacent decision knowledge elements and their respective links that can be
	 * directly reached from the root element (with link distance 1).
	 *
	 * @see DecisionKnowledgeElement
	 * @see Link
	 * @param element
	 *            decision knowledge element in the graph
	 * @return map of adjacent decision knowledge elements and the respective links in the
	 *         graph.
	 */
	Map<DecisionKnowledgeElement, Link> getAdjacentElementsAndLinks(DecisionKnowledgeElement element);

	/**
	 * Get the adjacent decision knowledge elements that can be directly reached from the root
	 * element (with link distance 1).
	 *
	 * @see DecisionKnowledgeElement
	 * @param element
	 *            decision knowledge element in the graph
	 * @return list of adjacent decision knowledge elements in the graph.
	 */
	List<DecisionKnowledgeElement> getAdjacentElements(DecisionKnowledgeElement element);
	
	/**
	 * Return all decision knowledge elements within the graph as a list. The list
	 * is not sorted.
	 *
	 * @see DecisionKnowledgeElement
	 */
	List<DecisionKnowledgeElement> getAllElements();

	/**
	 * Get the project that this graph of decision knowledge elements belongs to.
	 * The project is a JIRA project that is extended with settings for this
	 * plug-in, for example, whether the plug-in is activated for the project.
	 *
	 * @see DecisionKnowledgeProject
	 * @return project.
	 */
	DecisionKnowledgeProject getProject();

	/**
	 * Set the project that this graph of decision knowledge elements belongs to.
	 * The project is a JIRA project that is extended with settings for this
	 * plug-in, for example, whether the plug-in is activated for the project.
	 *
	 * @see DecisionKnowledgeProject
	 * @param project
	 *            decision knowledge project.
	 */
	void setProject(DecisionKnowledgeProject project);
}
