package de.uhd.ifi.se.decision.management.jira.model;

import java.util.Set;

import org.jgrapht.Graph;

/**
 * Interface to create a knowledge graph for the entire project or a sub-graph
 * from a given start node with a certain distance (set in the constructor). The
 * knowledge covers decision knowledge, JIRA issues such as requirements and
 * work items, commits, files (e.g., Java classes), and methods. Extends the
 * JGraphT graph interface.
 * 
 * @see GitClient
 * @see Graph
 */
public interface KnowledgeGraph extends Graph<Node, Link> {

	/**
	 *
	 * @param subRootNode
	 * @return a part of the project graph as as new graph
	 */
	Graph<Node, Link> getSubGraph(Node subRootNode);

	/**
	 * @return all nodes in the graph
	 */
	Set<Node> getAllNodes();

	/**
	 * @return all edges in the graph
	 */
	Set<Link> getAllEdges();
}
