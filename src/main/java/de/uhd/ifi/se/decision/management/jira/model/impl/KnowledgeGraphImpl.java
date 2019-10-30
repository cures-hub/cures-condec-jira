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
		}
		DecisionKnowledgeElement destination = link.getTarget();
		if (!containsVertex(destination)) {
			addVertex(destination);
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
		for (Node currentNode : vertexSet()) {
			if (node.getId() == currentNode.getId()) {
				return replaceVertex((DecisionKnowledgeElement) currentNode, node);
			}
		}
		return false;
	}

	private boolean replaceVertex(DecisionKnowledgeElement vertex, DecisionKnowledgeElement replace) {
		Set<Link> links = this.edgesOf(vertex);
		if (removeVertex(vertex)) {
			addVertex(replace);
		} else {
			LOGGER.error("Change of the nodes was not possible. Update failed");
			return false;
		}
		// Replace the old with the new vertex
		for (Link link : links) {
			removeEdge(link);
			if (link.getTarget().getId() == vertex.getId()) {
				link.setDestinationElement(replace);
				addEdge(link);
			} else {
				link.setSourceElement(replace);
				addEdge(link);
			}
		}
		return true;
	}

	@Override
	public boolean containsEdge(Link link) {
		return super.containsEdge(link.getSource(), link.getTarget());
	}
	//
	// @Override
	// public boolean containsVertex(Node node) {
	// for (Node setNode : vertexSet()) {
	// if (setNode.getId() == node.getId()
	// && setNode.getDocumentationLocation() == node.getDocumentationLocation()) {
	// return true;
	// }
	// }
	// return false;
	// }

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
