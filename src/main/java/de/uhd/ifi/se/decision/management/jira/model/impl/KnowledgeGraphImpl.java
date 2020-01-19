package de.uhd.ifi.se.decision.management.jira.model.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.AbstractPersistenceManagerForSingleLocation;

public class KnowledgeGraphImpl extends DirectedWeightedMultigraph<KnowledgeElement, Link> implements KnowledgeGraph {

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
		addElements();
		addEdges();
	}

	private void addElements() {
		List<KnowledgeElement> elements = persistenceManager.getDecisionKnowledgeElements();
		for (KnowledgeElement element : elements) {
			addVertex(element);
		}
	}

	private void addEdges() {
		for (KnowledgeElement element : this.vertexSet()) {
			AbstractPersistenceManagerForSingleLocation manager = persistenceManager
					.getManagerForSingleLocation(element.getDocumentationLocation());
			List<Link> links = manager.getLinks(element.getId());
			for (Link link : links) {
				KnowledgeElement destination = link.getTarget();
				KnowledgeElement source = link.getSource();
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
		KnowledgeElement source = link.getSource();
		addVertex(source);

		KnowledgeElement destination = link.getTarget();
		addVertex(destination);

		try {
			isEdgeCreated = this.addEdge(source, destination, link);
		} catch (IllegalArgumentException e) {
			LOGGER.error("Error adding link to the graph: " + e.getMessage());
		}
		return isEdgeCreated;
	}

	@Override
	public boolean updateElement(KnowledgeElement node) {
		KnowledgeElement oldElement = null;
		for (KnowledgeElement currentElement : vertexSet()) {
			if (node.equals(currentElement)) {
				oldElement = currentElement;
			}
		}
		if (oldElement == null) {
			return false;
		}
		return replaceVertex(oldElement, node);
	}

	private boolean replaceVertex(KnowledgeElement vertex, KnowledgeElement replace) {
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
	public Set<Link> edgesOf(KnowledgeElement element) {
		Set<Link> edges = new HashSet<Link>();
		try {
			edges = super.edgesOf(element);
		} catch (IllegalArgumentException e) {
			LOGGER.error("Edges for node could not be returned. " + e);
		}
		return edges;
	}

	@Override
	public List<KnowledgeElement> getUnlinkedElements(KnowledgeElement element) {
		List<KnowledgeElement> elements = new ArrayList<KnowledgeElement>();
		elements.addAll(this.vertexSet());
		if (element == null) {
			return elements;
		}
		elements.remove(element);

		List<KnowledgeElement> linkedElements = Graphs.neighborListOf(this, element);
		elements.removeAll(linkedElements);

		return elements;
	}

}
