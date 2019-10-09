package de.uhd.ifi.se.decision.management.jira.model.impl;

import java.util.*;

import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueTextPersistenceManager;
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

	private List getElementsInProject() {
		AbstractPersistenceManager strategy = AbstractPersistenceManager.getDefaultPersistenceStrategy(project.getProjectKey());
		List<DecisionKnowledgeElement> elements = strategy.getDecisionKnowledgeElements();
		AbstractPersistenceManager jiraIssueCommentPersistenceManager = new JiraIssueTextPersistenceManager(project.getProjectKey());
		elements.addAll(jiraIssueCommentPersistenceManager.getDecisionKnowledgeElements());
		return elements;
	}

	private void addNodes() {
		List<Node> nodesList = getElementsInProject();
		ListIterator<Node> iterator = nodesList.listIterator();
		while (iterator.hasNext()) {
			Node node = iterator.next();
			if(!this.containsVertex(node)){
				this.addVertex(node);
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
					if(this.containsVertex(link.getDestinationElement()) && this.containsVertex(link.getSourceElement())) {
						this.addEdge(link.getSourceElement(), link.getDestinationElement());
					}
				}
			}
		}
	}
}
