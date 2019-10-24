package de.uhd.ifi.se.decision.management.jira.model;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.jgrapht.Graph;

import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.model.impl.KnowledgeGraphImpl;

/**
 * Interface to create a knowledge graph for the entire project or a sub-graph
 * from a given start node with a certain distance (set in the constructor). The
 * knowledge covers decision knowledge, JIRA issues such as requirements and
 * work items, commits, files (e.g., Java classes), and methods. Extends the
 * JGraphT graph interface. The knowledge graph can be disconnected.
 * 
 * @see GitClient
 * @see Graph
 */
@JsonDeserialize(as = KnowledgeGraphImpl.class)
public interface KnowledgeGraph extends Graph<Node, Link> {

	/**
	 * Instances of knowledge graphs that are identified by the project key.
	 */
	static Map<String, KnowledgeGraph> instances = new HashMap<String, KnowledgeGraph>();

	/**
	 * Retrieves an existing KnowledgeGraph instance or creates a new instance if
	 * there is no instance for the given project key.
	 * 
	 * @param projectKey
	 *            of the Jira project.
	 * @return either a new or already existing KnowledgeGraph instance.
	 * 
	 * @see KnowledgeGraphImpl
	 */
	static KnowledgeGraph getOrCreate(String projectKey) {
		if (projectKey == null || projectKey.isBlank()) {
			return null;
		}
		if (instances.containsKey(projectKey)) {
			return instances.get(projectKey);
		}
		KnowledgeGraph knowledgeGraph = new KnowledgeGraphImpl(projectKey);
		instances.put(projectKey, knowledgeGraph);
		return knowledgeGraph;
	}

	/**
	 * Adds the specified edge ({@link Link} object) to this graph, going from the
	 * source vertex to the target vertex. More formally, adds the specified edge,
	 * <code>e</code>, to this graph if this graph contains no edge <code>e2</code>
	 * such that <code>e2.equals(e)</code>. If this graph already contains such an
	 * edge, the call leaves this graph unchanged and returns <tt>false</tt>. If the
	 * edge was added to the graph, returns <code>true</code>.
	 *
	 * @param link
	 *            edge to be added to this graph as a {@link Link} object.
	 *
	 * @return <tt>true</tt> if this graph did not already contain the specified
	 *         edge.
	 *
	 * @see #addEdge(Object, Object)
	 * @see #getEdgeSupplier()
	 */
	boolean addEdge(Link link);

	/**
	 * Updates a node. If it is not in the graph it will be added.
	 * 
	 * @param node
	 */
	void updateNode(Node node);
}
