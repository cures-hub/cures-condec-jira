package de.uhd.ifi.se.decision.management.jira.view.treant;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.jgrapht.Graph;
import org.jgrapht.graph.AsUndirectedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.FilteringManager;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;

/**
 * Creates a tree data structure from the {@link KnowledgeGraph} according to
 * the given {@link FilterSettings}. Uses the Treant.js framework for
 * visualization of the knowledge tree.
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
	private FilterSettings filterSettings;
	@JsonIgnore
	private boolean isHyperlinked;

	public Treant(FilterSettings filterSettings) {
		this(filterSettings, false);
	}

	public Treant(FilterSettings filterSettings, boolean isHyperlinked) {
		if (filterSettings == null) {
			return;
		}
		this.filterSettings = filterSettings;

		FilteringManager filteringManager = new FilteringManager(filterSettings);
		this.graph = filteringManager.getSubgraphMatchingFilterSettings();
		this.setChart(new Chart());
		nodeStructure = getTreantNodeWithChildren(filterSettings.getSelectedElement());
		this.setHyperlinked(isHyperlinked);
	}

	public Treant(String treantId, FilterSettings filterSettings) {
		this(filterSettings, false);
		// TODO Why is the treantId needed?
		this.setChart(new Chart(treantId));
	}

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

		while (iterator.hasNext()) {
			KnowledgeElement childElement = iterator.next();

			// TODO Add to FilteringManager
			if (filterSettings.isOnlyDecisionKnowledgeShown() && childElement.getType() == KnowledgeType.OTHER) {
				continue;
			}

			KnowledgeElement parentElement = iterator.getParent(childElement);
			if (parentElement == null) {
				continue;
			}
			Link edge = undirectedGraph.getEdge(childElement, parentElement);

			TreantNode childNode = new TreantNode(childElement, edge, false, isHyperlinked);
			elementToTreantNodeMap.put(childElement, childNode);

			TreantNode parentNode = elementToTreantNodeMap.get(parentElement);
			if (parentNode == null) {
				continue;
			}

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

	public FilterSettings getFilterSettings() {
		return filterSettings;
	}

	public void setFilterSettings(FilterSettings filterSettings) {
		this.filterSettings = filterSettings;
	}
}