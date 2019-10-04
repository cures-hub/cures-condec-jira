package de.uhd.ifi.se.decision.management.jira.model.impl;

import de.uhd.ifi.se.decision.management.jira.model.*;
import de.uhd.ifi.se.decision.management.jira.persistence.AbstractPersistenceManager;
import org.jgrapht.Graph;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.jgrapht.graph.Subgraph;

import java.util.*;

public class KnowledgeGraphImpl extends DirectedWeightedMultigraph<Node, Link> implements KnowledgeGraph {

	private DecisionKnowledgeProject project;
	private Set<Node> allNodes;
	private Set<Link> allEdges;

	public KnowledgeGraphImpl(String projectKey) {
		super(LinkImpl.class);
		this.project = new DecisionKnowledgeProjectImpl(projectKey);
		createGraph();
	}

	@Override
	public KnowledgeGraph getSubGraph(Node subRootNode) {
		//TODO Compute subset of Nodes
		//TODO Test first
		Graph subGraph = new Subgraph<Node, Link, KnowledgeGraph>(this, allNodes, allEdges);
		return (KnowledgeGraph) subGraph;
	}

	private void createGraph() {
		addNodes();
		addEdges();
	}

	private void addNodes() {
		allNodes = new HashSet<>();
		List<Node> nodesList = new ArrayList<>();
		ListIterator<Node> iterator = nodesList.listIterator();
		while (iterator.hasNext()) {
			for (Node adjacentNode : this.getAdjacentElements(iterator.next())) {
				if (!this.containsVertex(adjacentNode)) {
					iterator.add(adjacentNode);
					iterator.previous();
				}
			}
		}
		allNodes.addAll(nodesList);
	}

	private void addEdges() {
		allEdges = new HashSet<>();
		for (Node node : allNodes) {
			AbstractPersistenceManager manager = AbstractPersistenceManager.getPersistenceManager(project.getProjectKey(), node.getDocumentationLocation());
			List<Link> links = manager.getLinks((DecisionKnowledgeElement)node);
			for (Link link : links) {
				if (!this.containsEdge(link)) {
					allEdges.add(link);
				}
			}
		}
	}

	private List<Node> getAdjacentElements(Node node) {
		AbstractPersistenceManager manager = AbstractPersistenceManager.getPersistenceManager(project.getProjectKey(), node.getDocumentationLocation());
		List<Node> adjacentNodes = new ArrayList<>();
		for (DecisionKnowledgeElement element : manager.getAdjacentElements(node.getId())) {
			adjacentNodes.add(new NodeImpl(element));
		}
		return adjacentNodes;
	}
}
