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
	private Node rootNode;

	public KnowledgeGraphImpl(String projectKey) {
		super(LinkImpl.class);
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
		return this.vertexSet();
	}

	@Override
	public Set<Link> getAllEdges() {
		return this.edgeSet();
	}

	private void createGraph() {
		addNodes();
		addEdges();
	}

	private void addNodes() {
		AbstractPersistenceManager manager = project.getPersistenceStrategy();
		List<Node> nodesList = computeToNode(manager.getDecisionKnowledgeElements());
		ListIterator<Node> iterator = nodesList.listIterator();
		while (iterator.hasNext()) {
			for (Node adjacentNode : this.getAdjacentElements(iterator.next())) {
				if (!this.containsVertex(adjacentNode)) {
					this.addVertex(adjacentNode);
					iterator.add(adjacentNode);
					iterator.previous();
				}
			}
		}
	}

	private void addEdges() {
		for (Node node : this.vertexSet()) {
			AbstractPersistenceManager manager = AbstractPersistenceManager
					.getPersistenceManager(project.getProjectKey(), node.getDocumentationLocation());
			List<Link> links = manager.getLinks(node.getId());
			for (Link link : links) {
				if (!this.containsEdge(link)) {
					this.addEdge(link.getSourceElement(), link.getDestinationElement());
				}
			}
		}
	}

	private List<Node> getAdjacentElements(Node node) {
		AbstractPersistenceManager manager = AbstractPersistenceManager.getPersistenceManager(project.getProjectKey(),
				node.getDocumentationLocation());
		return computeToNode(manager.getAdjacentElements(node.getId()));
	}

	private List<Node> computeToNode(List<DecisionKnowledgeElement> elements) {
		List<Node> nodeList = new ArrayList<>();
		for(DecisionKnowledgeElement element: elements){
			nodeList.add(element);
		}
		return nodeList;
	}
}
