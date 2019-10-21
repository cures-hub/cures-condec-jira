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

	Map<DecisionKnowledgeElement, Link> getAdjacentElementsAndLinks(DecisionKnowledgeElement element);

	void addEdge(Link link);
}
