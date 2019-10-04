package de.uhd.ifi.se.decision.management.jira.model;

import org.jgrapht.Graph;

import java.util.List;
import java.util.Set;

public interface KnowledgeGraph  extends Graph<Node, Link> {

    /**
     *
     * @param subRootNode
     * @return a part of the project graph as as new graph
     */
    KnowledgeGraph getSubGraph(Node subRootNode);

    /**
     * @return all nodes in the graph
     */
    Set<Node> getAllNodes();

    /**
      * @return all edges in the graph
     */
    Set<Link> getAllEdges();
}
