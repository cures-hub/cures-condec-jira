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
	private Node rootNode;

	public KnowledgeGraphImpl(String projectKey, DecisionKnowledgeElement rootElement) {
		super(LinkImpl.class);
		this.rootNode = new NodeImpl(rootElement);
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

	@Override
	public Set<Node> getAllNodes() {
		return allNodes;
	}

	public Set<Link> getAllEdges() {
		return allEdges;
	}

	private void createGraph() {
		addNodes();
		addEdges();
	}

	private void addNodes() {
		allNodes = new HashSet<>();
		List<Node> nodesList = new ArrayList<>();
		nodesList.add(rootNode);
		ListIterator<Node> iterator = nodesList.listIterator();
		while (iterator.hasNext()) {
			for (Node adjacentNode : this.getAdjacentElements(iterator.next())) {
				if (!this.containsVertex(adjacentNode)) {
					this.allNodes.add(adjacentNode);
					this.addVertex(adjacentNode);
					iterator.add(adjacentNode);
					iterator.previous();
				}
			}
		}
	}

	private void addEdges() {
		allEdges = new HashSet<>();
		for (Node node : allNodes) {
			AbstractPersistenceManager manager = AbstractPersistenceManager.getPersistenceManager(project.getProjectKey(), node.getDocumentationLocation());
			List<Link> links = manager.getLinks(node.getId());
			for (Link link : links) {
				if (!this.containsEdge(link)) {
					this.addEdge(new NodeImpl(link.getSourceElement()), new NodeImpl(link.getDestinationElement()));
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

	@Override
	public boolean containsVertex(Node node) {
		for (Node containNode : allNodes) {
			if (containNode.getId() == node.getId()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean containsEdge(Link link) {
		for (Link containLink : allEdges) {
			if (containLink.getId() == link.getId()) {
				return true;
			}
		}
		return false;
	}
}
