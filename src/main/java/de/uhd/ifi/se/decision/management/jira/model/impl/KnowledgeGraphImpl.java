package de.uhd.ifi.se.decision.management.jira.model.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

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
	public void updateNode(Node node) {
		if (!this.containsVertex(node)) {
			this.addVertex(node);
		} else {
			Iterator<Node> iter = this.vertexSet().iterator();
			while (iter.hasNext()) {
				Node iterNode = iter.next();
				/*
				 * the node need to be updated. To update the node we remove the old element and
				 * add the new. the ids need to be the same. the vertex set has no get function
				 * because of this we iterate the set
				 */
				if (iterNode.getId() == node.getId()) {
					this.vertexSet().remove(iterNode);
					this.vertexSet().add(node);
					break;
				}
			}
		}
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
		if (removedLink == null) {
			return false;
		}
		return true;
	}
}
