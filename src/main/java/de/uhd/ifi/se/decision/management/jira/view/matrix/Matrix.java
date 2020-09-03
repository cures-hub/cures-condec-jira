package de.uhd.ifi.se.decision.management.jira.view.matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.bind.annotation.XmlElement;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.jgrapht.Graph;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.FilteringManager;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.LinkType;

/**
 * Creates an adjacency matrix of the {@link KnowledgeGraph}. The matrix can
 * either be created on the entire graph or on a subgraph matching the giving
 * {@link FilterSettings}. The subgraph is provided by the
 * {@link FilteringManager}.
 * 
 * If you want to change the shown (sub-)graph, do not change this class but
 * change the {@link FilteringManager} and/or the {@link KnowledgeGraph}.
 */
public class Matrix {
	@XmlElement
	private Set<KnowledgeElement> headerElements;

	@JsonIgnore
	private Graph<KnowledgeElement, Link> graph;

	public Matrix(String projectKey, Set<KnowledgeElement> elements) {
		this.headerElements = elements;
		graph = KnowledgeGraph.getOrCreate(projectKey);
	}

	public Matrix(FilterSettings filterSettings) {
		FilteringManager filteringManager = new FilteringManager(filterSettings);
		graph = filteringManager.getSubgraphMatchingFilterSettings();
		this.headerElements = graph.vertexSet();
	}

	public Set<KnowledgeElement> getHeaderElements() {
		return headerElements;
	}

	@XmlElement(name = "coloredRows")
	public List<List<String>> getColoredRows() {
		List<List<String>> coloredRows = new ArrayList<>();
		for (KnowledgeElement sourceElement : headerElements) {
			List<String> row = getColoredRow(sourceElement);
			coloredRows.add(row);
		}
		return coloredRows;
	}

	public List<String> getColoredRow(KnowledgeElement sourceElement) {
		List<String> row = new ArrayList<String>();

		for (KnowledgeElement targetElement : headerElements) {
			if (targetElement.getId() == sourceElement.getId()) {
				row.add("LightGray");
				continue;
			}
			Link linkToTargetElement = sourceElement.getOutgoingLink(targetElement);
			if (linkToTargetElement != null) {
				row.add(LinkType.getLinkTypeColor(linkToTargetElement.getType()));
			} else {
				row.add("White");
			}
		}
		return row;
	}

	/**
	 * Used to plot the legend for relationship types in the frontend.
	 * 
	 * @return map of link type names and colors. Also contains Jira issue link
	 *         types.
	 */
	@XmlElement(name = "linkTypesWithColor")
	public Map<String, String> getLinkTypesWithColor() {
		Map<String, String> linkTypesWithColor = new TreeMap<>();
		for (String linkTypeName : DecisionKnowledgeProject.getNamesOfLinkTypes()) {
			linkTypesWithColor.put(linkTypeName, LinkType.getLinkTypeColor(linkTypeName));
		}
		return linkTypesWithColor;
	}
}