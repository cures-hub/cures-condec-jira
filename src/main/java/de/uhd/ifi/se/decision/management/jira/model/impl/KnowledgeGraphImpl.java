package de.uhd.ifi.se.decision.management.jira.model.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.jgrapht.traverse.BreadthFirstIterator;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.Node;
import de.uhd.ifi.se.decision.management.jira.persistence.PersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.AbstractPersistenceManagerForSingleLocation;

public class KnowledgeGraphImpl extends DirectedWeightedMultigraph<Node, Link> implements KnowledgeGraph {

	private static final long serialVersionUID = 1L;
	private DecisionKnowledgeProject project;
	protected List<Long> linkIds;

	public KnowledgeGraphImpl(String projectKey) {
		super(LinkImpl.class);
		linkIds = new ArrayList<Long>();
		this.project = new DecisionKnowledgeProjectImpl(projectKey);
		createGraph();
	}

	@Override
	public Map<DecisionKnowledgeElement, Link> getAdjacentElementsAndLinks(DecisionKnowledgeElement element) {
		BreadthFirstIterator<Node, Link> iterator = new BreadthFirstIterator<>(this, element);
		// Use First element as parent node
		Node parentNode = iterator.next();
		Map<DecisionKnowledgeElement, Link> childrenAndLinks = new HashMap<>();
		while (iterator.hasNext()) {
			Node iterNode = iterator.next();
			if (iterator.getParent(iterNode).getId() == parentNode.getId()
					&& iterNode instanceof DecisionKnowledgeElement) {
				DecisionKnowledgeElement nodeElement = (DecisionKnowledgeElement) iterNode;
				childrenAndLinks.put(nodeElement, this.getEdge(parentNode, iterNode));
			}
		}
		return childrenAndLinks;
	}

	private void createGraph() {
		addNodes();
		addEdges();
	}

	private void addNodes() {
		List<DecisionKnowledgeElement> nodesList = PersistenceManager.getOrCreate(project.getProjectKey())
				.getDecisionKnowledgeElements();
		ListIterator<DecisionKnowledgeElement> iterator = nodesList.listIterator();
		while (iterator.hasNext()) {
			Node node = iterator.next();
			this.addVertex(node);
		}
	}

	private void addEdges() {
		for (Node node : this.vertexSet()) {
			AbstractPersistenceManagerForSingleLocation manager = PersistenceManager
					.getOrCreate(project.getProjectKey()).getPersistenceManager(node.getDocumentationLocation());
			List<Link> links = manager.getLinks(node.getId());
			for (Link link : links) {
				Node destination = link.getDestinationElement();
				Node source = link.getSourceElement();
				if (destination == null || source == null) {
					continue;
				}
				if (destination.equals(source)) {
					continue;
				}
				if (!linkIds.contains(link.getId()) && this.containsVertex(link.getDestinationElement())
						&& this.containsVertex(link.getSourceElement())) {
					this.addEdge(link);
					linkIds.add(link.getId());
				}
			}
		}
	}

	@Override
	public void addEdge(Link link) {
		DecisionKnowledgeElement source = link.getSourceElement();
		if (!containsVertex(source)) {
			addVertex(source);
		}
		DecisionKnowledgeElement destination = link.getDestinationElement();
		if (!containsVertex(destination)) {
			addVertex(destination);
		}
		try {
			this.addEdge(source, destination, link);
		} catch (IllegalArgumentException e) {

		}
	}
}
