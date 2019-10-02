package de.uhd.ifi.se.decision.management.jira.model.impl;

import de.uhd.ifi.se.decision.management.jira.model.*;
import de.uhd.ifi.se.decision.management.jira.persistence.AbstractPersistenceManager;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class KnowledgeGraphImpl extends DirectedWeightedMultigraph<Node, Link> implements KnowledgeGraph {

	private DecisionKnowledgeProject project;
	private Node rootNode;
	private List<Node> allNodes;
	private List<Link> allEdges;

	public KnowledgeGraphImpl(String projectKey, DecisionKnowledgeElement rootElement) {
		super(LinkImpl.class);
		this.project = new DecisionKnowledgeProjectImpl(projectKey);
		this.rootNode = new NodeImpl(rootElement);
		createGraph();
	}

	private void createGraph() {
		addNodes();
		addEdges();
	}

	private void addNodes() {
		allNodes = new ArrayList<>();
		allNodes.add(rootNode);
		ListIterator<Node> iterator = allNodes.listIterator();
		while (iterator.hasNext()) {
			for (Node adjacentNode : this.getAdjacentElements(iterator.next())) {
				if (!this.containsVertex(adjacentNode)) {
					iterator.add(adjacentNode);
					iterator.previous();
				}
			}
		}
	}

	private void addEdges() {
		for (Node node : allNodes) {
			AbstractPersistenceManager manager = AbstractPersistenceManager.getPersistenceManager(project.getProjectKey(), node.getDocumentationLocation());
			//TODO Implement with id or change to DecisionKnowledgeElement
			List<Link> links = manager.getLinks(node);
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
