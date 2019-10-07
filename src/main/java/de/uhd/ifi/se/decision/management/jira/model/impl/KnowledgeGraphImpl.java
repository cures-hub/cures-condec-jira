package de.uhd.ifi.se.decision.management.jira.model.impl;

import java.util.*;

import org.jgrapht.Graph;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.Node;
import de.uhd.ifi.se.decision.management.jira.persistence.AbstractPersistenceManager;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;

public class KnowledgeGraphImpl extends DirectedWeightedMultigraph<Node, Link> implements KnowledgeGraph {

	private static final long serialVersionUID = 1L;
	private DecisionKnowledgeProject project;

	private Set<Node> allNodes;
	private Set<Link> allEdges;
	private Node rootNode;

	public KnowledgeGraphImpl(String projectKey, DecisionKnowledgeElement rootElement) {
		super(LinkImpl.class);
		this.rootNode = rootElement;
		this.project = new DecisionKnowledgeProjectImpl(projectKey);
		createGraph();
	}

	public KnowledgeGraphImpl(DecisionKnowledgeProject project, Node rootNode) {
		super(LinkImpl.class);
		this.rootNode = rootNode;
		this.project = project;
	}

	@Override
	public KnowledgeGraph getSubGraph(Node subRootNode) {
		KnowledgeGraph subGraph = new KnowledgeGraphImpl(this.project, subRootNode);
		Iterator<Node> iterator = new BreadthFirstIterator<>(this);
		while (iterator.hasNext()) {
			Node iterNode = iterator.next();
			if(iterNode.getId() == subRootNode.getId()){
				Iterator<Node> subIterator = new DepthFirstIterator<>(this, iterNode);
				while (subIterator.hasNext()) {
					Node subNodeIt = subIterator.next();
					subGraph.addVertex(subNodeIt);
				}
				break;
			}
		}
		return subGraph;
	}

	@Override
	public Set<Node> getAllNodes() {
		return allNodes;
	}

	@Override
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
			AbstractPersistenceManager manager = AbstractPersistenceManager
					.getPersistenceManager(project.getProjectKey(), node.getDocumentationLocation());
			List<Link> links = manager.getLinks(node.getId());
			for (Link link : links) {
				if (!this.containsEdge(link)) {
					this.addEdge(link.getSourceElement(), link.getDestinationElement());
					allEdges.add(link);
				}
			}
		}
	}

	private List<Node> getAdjacentElements(Node node) {
		AbstractPersistenceManager manager = AbstractPersistenceManager.getPersistenceManager(project.getProjectKey(),
				node.getDocumentationLocation());
		List<Node> adjacentNodes = new ArrayList<>();
		for (DecisionKnowledgeElement element : manager.getAdjacentElements(node.getId())) {
			adjacentNodes.add(element);
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
