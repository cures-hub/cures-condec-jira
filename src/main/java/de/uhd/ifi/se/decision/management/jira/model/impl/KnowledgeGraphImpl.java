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
	public boolean addEdge(Link link) {
		boolean isEdgeCreated = false;
		DecisionKnowledgeElement source = link.getSourceElement();
		if (!containsVertex(source)) {
			addVertex(source);
		}
		DecisionKnowledgeElement destination = link.getDestinationElement();
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
	public void updateNode(Node node) {
		if(!this.containsVertex(node)){
			this.addVertex(node);
		} else {
			Iterator<Node> iter = this.vertexSet().iterator();
			while (iter.hasNext()) {
				Node iterNode = iter.next();
				/*  the node need to be updated. To update the node we remove the old element and add the new.
					the ids need to be the same. the vertex set has no get function because of this we iterate the set
				 */
				if(iterNode.getId() == node.getId()) {
					this.vertexSet().remove(iterNode);
					this.vertexSet().add(node);
					break;
				}
			}
		}
	}

	@Override
	public void updateLink(Link link) {
		/*  If the links is not in the graph it will be added. If the link exists the link will be removed and the
			new version will be added again.
		*/
		if(!containsEdge(link)) {
			this.addEdge(link);
		} else {
			this.removeEdge(link.getSource(), link.getTarget());
			this.addEdge(link);
		}
	}
}
