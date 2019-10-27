package de.uhd.ifi.se.decision.management.jira.model.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.Node;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.AbstractPersistenceManagerForSingleLocation;

public class KnowledgeGraphImpl extends DirectedWeightedMultigraph<Node, Link> implements KnowledgeGraph {

	private static final Logger LOGGER = LoggerFactory.getLogger(KnowledgeGraphImpl.class);

	private static final long serialVersionUID = 1L;
	private DecisionKnowledgeProject project;
	protected List<Long> linkIds;

	public KnowledgeGraphImpl(String projectKey) {
		super(LinkImpl.class);
		linkIds = new ArrayList<Long>();
		this.project = new DecisionKnowledgeProjectImpl(projectKey);
		createGraph();
	}

	private void createGraph() {
		addNodes();
		addEdges();
	}

	private void addNodes() {
		List<DecisionKnowledgeElement> nodesList = KnowledgePersistenceManager.getOrCreate(project.getProjectKey())
				.getDecisionKnowledgeElements();
		ListIterator<DecisionKnowledgeElement> iterator = nodesList.listIterator();
		while (iterator.hasNext()) {
			Node node = iterator.next();
			this.addVertex(node);
		}
	}

	private void addEdges() {
		for (Node node : this.vertexSet()) {
			AbstractPersistenceManagerForSingleLocation manager = KnowledgePersistenceManager
					.getOrCreate(project.getProjectKey()).getPersistenceManager(node.getDocumentationLocation());
			List<Link> links = manager.getLinks(node.getId());
			for (Link link : links) {
				Node destination = link.getTarget();
				Node source = link.getSource();
				if (destination == null || source == null) {
					continue;
				}
				if (destination.equals(source)) {
					continue;
				}
				if (!linkIds.contains(link.getId()) && this.containsVertex(link.getTarget())
						&& this.containsVertex(link.getSource())) {
					this.addEdge(link);
					linkIds.add(link.getId());
				}
			}
		}
	}

	@Override
	public boolean addEdge(Link link) {
		boolean isEdgeCreated = false;
		DecisionKnowledgeElement source = link.getSource();
		if (!containsVertex(source)) {
			addVertex(source);
			System.out.println("Source node was created in graph");
		}
		DecisionKnowledgeElement destination = link.getTarget();
		if (!containsVertex(destination)) {
			addVertex(destination);
			System.out.println("Destination node was created in graph");
		}
		try {
			isEdgeCreated = this.addEdge(source, destination, link);
		} catch (IllegalArgumentException e) {
			LOGGER.error("Error adding link to the graph: " + e.getMessage());
		}
		return isEdgeCreated;
	}

	@Override
	public boolean updateNode(DecisionKnowledgeElement node) {
		DecisionKnowledgeElement oldNode = null;
		for (Node currentNode : vertexSet()) {
			if (node.getId() == currentNode.getId()) {
				oldNode = (DecisionKnowledgeElement) currentNode;
			}
		}
		if (oldNode == null) {
			return false;
		}
		return replaceVertex(oldNode, node);
	}

	private boolean replaceVertex(DecisionKnowledgeElement vertex, DecisionKnowledgeElement replace) {
		addVertex(replace);
		for (Link edge : outgoingEdgesOf(vertex)) {
			addEdge(replace, edge.getTarget(), edge);
		}
		for (Link edge : incomingEdgesOf(vertex)) {
			addEdge(edge.getSource(), replace, edge);
		}
		for (Link edge : edgesOf(vertex)) {
			removeEdge(edge);
		}
		return removeVertex(vertex);
	}

	@Override
	public boolean containsEdge(Link link) {
		return super.containsEdge(link.getSource(), link.getTarget());
	}

	@Override
	public boolean removeEdge(Link link) {
		Link removedLink = super.removeEdge(link.getSource(), link.getTarget());
		if (removedLink == null) {
			removedLink = super.removeEdge(link.getTarget(), link.getSource());
		}
		return removedLink != null;
	}

	@Override
	public Set<Link> edgesOf(Node node) {
		Set<Link> edges = new HashSet<Link>();
		try {
			edges = super.edgesOf(node);
		} catch (IllegalArgumentException e) {
			LOGGER.error("Edges for node could not be returned. " + e);
		}
		return edges;
	}
}
