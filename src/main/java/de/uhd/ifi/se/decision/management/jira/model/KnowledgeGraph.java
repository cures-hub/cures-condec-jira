package de.uhd.ifi.se.decision.management.jira.model;

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
 * JGraphT graph interface.
 * 
 * @see GitClient
 * @see Graph
 */
@JsonDeserialize(as = KnowledgeGraphImpl.class)
public interface KnowledgeGraph extends Graph<Node, Link> {

	Map<DecisionKnowledgeElement, Link> getAdjacentElementsAndLinks(DecisionKnowledgeElement element);
}
