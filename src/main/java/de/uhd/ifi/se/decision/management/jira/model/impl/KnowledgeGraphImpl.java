package de.uhd.ifi.se.decision.management.jira.model.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.Node;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.AbstractPersistenceManagerForSingleLocation;

public class KnowledgeGraphImpl extends DirectedWeightedMultigraph<Node, Link> implements KnowledgeGraph {

	private static final Logger LOGGER = LoggerFactory.getLogger(KnowledgeGraphImpl.class);

	private static final long serialVersionUID = 1L;
	protected List<Long> linkIds;
	private KnowledgePersistenceManager persistenceManager;

	public KnowledgeGraphImpl(String projectKey) {
		super(LinkImpl.class);
		this.linkIds = new ArrayList<Long>();
		this.persistenceManager = KnowledgePersistenceManager.getOrCreate(projectKey);
		createGraph();
	}

	private void createGraph() {
		addNodes();
		addEdges();
	}

	private void addNodes() {
		List<DecisionKnowledgeElement> elements = persistenceManager.getDecisionKnowledgeElements();
		for (DecisionKnowledgeElement element : elements) {
			addVertex(element);
		}
	}

	private void addEdges() {
		for (Node node : this.vertexSet()) {
			AbstractPersistenceManagerForSingleLocation manager = persistenceManager
					.getPersistenceManager(node.getDocumentationLocation());
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
		addVertex(source);

		DecisionKnowledgeElement destination = link.getTarget();
		addVertex(destination);

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
			if (node.equals(currentNode)) {
				oldNode = (DecisionKnowledgeElement) currentNode;
				System.out.println(oldNode.getDocumentationLocation());
				System.out.println(node.getDocumentationLocation());
				System.out.println(oldNode.getId());
				System.out.println(node.getId());
			}
		}
		if (oldNode == null) {
			return false;
		}
		return replaceVertex(oldNode, node);
	}

	private boolean replaceVertex(DecisionKnowledgeElement vertex, DecisionKnowledgeElement replace) {
		Set<Link> newLinks = new HashSet<Link>();
		for (Link edge : outgoingEdgesOf(vertex)) {
			newLinks.add(new LinkImpl(replace, edge.getTarget()));
		}
		for (Link edge : incomingEdgesOf(vertex)) {
			newLinks.add(new LinkImpl(edge.getSource(), replace));
		}
		removeVertex(vertex);
		for (Link link : newLinks) {
			addEdge(link);
		}
		return true;
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
