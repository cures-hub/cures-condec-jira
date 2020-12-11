package de.uhd.ifi.se.decision.management.jira.view.treant;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.jgrapht.Graph;
import org.jgrapht.graph.AsUndirectedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.FilteringManager;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.Link;

/**
 * Creates a tree data structure from the {@link KnowledgeGraph} according to
 * the given {@link FilterSettings}. Uses the Treant.js framework for
 * visualization of the knowledge tree.
 * 
 * Iterates over the filtered {@link KnowledgeGraph} provided by the
 * {@link FilteringManager}.
 * 
 * If you want to change the shown (sub-)graph, do not change this class but
 * change the {@link FilteringManager} and/or the {@link KnowledgeGraph}.
 */
@XmlRootElement(name = "treant")
@XmlAccessorType(XmlAccessType.FIELD)
public class Treant {
	@XmlElement
	private Chart chart;

	@XmlElement
	private TreantNode nodeStructure;

	@JsonIgnore
	private Graph<KnowledgeElement, Link> graph;
	@JsonIgnore
	private boolean isHyperlinked;

	private static final Logger LOGGER = LoggerFactory.getLogger(Treant.class);

	public Treant(FilterSettings filterSettings) {
		this(filterSettings, false);
	}

	public Treant(FilterSettings filterSettings, boolean isHyperlinked) {
		if (filterSettings == null) {
			return;
		}
		LOGGER.info(filterSettings.toString());

		FilteringManager filteringManager = new FilteringManager(filterSettings);
		this.graph = filteringManager.getSubgraphMatchingFilterSettings();
		this.setChart(new Chart());
		nodeStructure = getTreantNodeWithChildren(filterSettings.getSelectedElement());
		this.setHyperlinked(isHyperlinked);
	}

	/**
	 * @issue How can graph iteration be done over both outgoing and incoming edges
	 *        of a knowledge element (=node)?
	 * @decision Convert the directed graph into an undirected graph for graph
	 *           iteration!
	 * 
	 * @issue How to handle loops in the graph?
	 * @decision Traverse all edges in the graph and duplicate knowledge elements to
	 *           resolve the loop! Store edge traversed by the breadth first
	 *           iterator and traverse the remaining edges afterwards!
	 * @alternative Only use breadth first iteration to traverse the graph!
	 * @con The breadth first iterator does not traverse all edges but every node
	 *      once. Criteria linked to many arguments would only be shown for one
	 *      argument in the tree, which is misleading and information loss.
	 */
	public TreantNode getTreantNodeWithChildren(KnowledgeElement rootElement) {
		if (rootElement == null || rootElement.getProject() == null) {
			return new TreantNode();
		}

		Map<KnowledgeElement, TreantNode> elementToTreantNodeMap = new HashMap<>();

		TreantNode rootNode = new TreantNode(rootElement, false, isHyperlinked);
		elementToTreantNodeMap.put(rootElement, rootNode);

		Graph<KnowledgeElement, Link> undirectedGraph = new AsUndirectedGraph<KnowledgeElement, Link>(graph);

		BreadthFirstIterator<KnowledgeElement, Link> iterator = new BreadthFirstIterator<>(undirectedGraph,
				rootElement);
		Set<Link> traversedEdges = new HashSet<>();

		while (iterator.hasNext()) {
			KnowledgeElement childElement = iterator.next();

			KnowledgeElement parentElement = iterator.getParent(childElement);
			if (parentElement == null) {
				continue;
			}
			Link edge = undirectedGraph.getEdge(childElement, parentElement);

			TreantNode childNode = new TreantNode(childElement, edge, false, isHyperlinked);
			elementToTreantNodeMap.put(childElement, childNode);

			TreantNode parentNode = elementToTreantNodeMap.get(parentElement);
			parentNode.getChildren().add(childNode);

			traversedEdges.add(edge);
		}

		Set<Link> remainingEdges = undirectedGraph.edgeSet().stream().filter(edge -> !traversedEdges.contains(edge))
				.collect(Collectors.toSet());

		for (Link edge : remainingEdges) {
			KnowledgeElement sourceElement = edge.getSource();
			KnowledgeElement targetElement = edge.getTarget();

			KnowledgeElement childElement = targetElement;
			TreantNode parentNode = elementToTreantNodeMap.get(sourceElement);
			if (parentNode == null) {
				parentNode = elementToTreantNodeMap.get(targetElement);
				childElement = sourceElement;
			}
			if (parentNode == null) {
				continue;
			}

			TreantNode childNode = new TreantNode(childElement, edge, false, isHyperlinked);
			elementToTreantNodeMap.put(childElement, childNode);
			parentNode.getChildren().add(childNode);
		}

		return rootNode;
	}

	public Chart getChart() {
		return chart;
	}

	public void setChart(Chart chart) {
		this.chart = chart;
	}

	public TreantNode getNodeStructure() {
		return nodeStructure;
	}

	public void setNodeStructure(TreantNode nodeStructure) {
		this.nodeStructure = nodeStructure;
	}

	public boolean isHyperlinked() {
		return isHyperlinked;
	}

	public void setHyperlinked(boolean isHyperlinked) {
		this.isHyperlinked = isHyperlinked;
	}
}