package de.uhd.ifi.se.decision.management.jira.model;

/**
 * Interface for nodes of the knowledge graph.
 * 
 * @see KnowledgeGraph
 * @see Link
 */
public interface Node {

	/**
	 * Gets the id of the node.
	 *
	 * @return id of the node.
	 */
	long getId();

	DocumentationLocation getDocumentationLocation();
}
