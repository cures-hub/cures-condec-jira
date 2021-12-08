package de.uhd.ifi.se.decision.management.jira.view.matrix;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.bind.annotation.XmlElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.KnowledgeElementWithImpact;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.FilteringManager;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.LinkType;
import de.uhd.ifi.se.decision.management.jira.quality.completeness.DefinitionOfDoneChecker;

/**
 * Creates an adjacency matrix of the {@link KnowledgeGraph}. The matrix can
 * either be created on the entire graph or on a subgraph matching the giving
 * {@link FilterSettings}. The subgraph is provided by the
 * {@link FilteringManager}.
 *
 * Each matrix cell contains a {@link Link} object or null if no link exists.
 *
 * If you want to change the shown (sub-)graph, do not change this class but
 * change the {@link FilteringManager} and/or the {@link KnowledgeGraph}.
 */
public class Matrix {

	private Set<ElementWithHighlighting> headerElementsWithHighlighting;
	private int size;
	private KnowledgeGraph filteredGraph;
	private static final Logger LOGGER = LoggerFactory.getLogger(Matrix.class);

	public Matrix(FilterSettings filterSettings, List<KnowledgeElementWithImpact> impactedElements) {
		LOGGER.info(filterSettings.toString());
		filteredGraph = new FilteringManager(filterSettings).getFilteredGraph(impactedElements);
		headerElementsWithHighlighting = new LinkedHashSet<>();
		filteredGraph.vertexSet().forEach(element -> {
			ElementWithHighlighting elementWithColors = new ElementWithHighlighting(element);
			if (filterSettings.areQualityProblemHighlighted()) {
				String problemExplanation = DefinitionOfDoneChecker.getQualityProblemExplanation(element,
						filterSettings);
				if (!problemExplanation.isEmpty()) {
					elementWithColors.setQualityColor("crimson");
					elementWithColors.setQualityProblemExplanation(problemExplanation);
				}
			}
			headerElementsWithHighlighting.add(elementWithColors);
		});
		size = headerElementsWithHighlighting.size();
	}

	public Matrix(FilterSettings filterSettings) {
		LOGGER.info(filterSettings.toString());
		filteredGraph = new FilteringManager(filterSettings).getFilteredGraph();
		headerElementsWithHighlighting = new LinkedHashSet<>();
		filteredGraph.vertexSet().forEach(element -> {
			ElementWithHighlighting elementWithColors = new ElementWithHighlighting(element);
			if (filterSettings.areQualityProblemHighlighted()) {
				String problemExplanation = DefinitionOfDoneChecker.getQualityProblemExplanation(element,
						filterSettings);
				if (!problemExplanation.isEmpty()) {
					elementWithColors.setQualityColor("crimson");
					elementWithColors.setQualityProblemExplanation(problemExplanation);
				}
			}
			headerElementsWithHighlighting.add(elementWithColors);
		});
		size = headerElementsWithHighlighting.size();
	}

	@XmlElement
	public Set<ElementWithHighlighting> getHeaderElementsWithHighlighting() {
		return headerElementsWithHighlighting;
	}

	/**
	 * Matrix of links for each cell.
	 */
	@XmlElement(name = "links")
	public Link[][] getMatrixOfLinks() {
		Link[][] links = new Link[size][size];
		Iterator<ElementWithHighlighting> iterator = headerElementsWithHighlighting.iterator();
		for (int positionY = 0; positionY < size; positionY++) {
			KnowledgeElement sourceElement = iterator.next().getElement();
			links[positionY] = getRowOfLinks(sourceElement);
		}
		return links;
	}

	public Link[] getRowOfLinks(KnowledgeElement sourceElement) {
		Link[] row = new Link[size];
		Iterator<ElementWithHighlighting> iterator = headerElementsWithHighlighting.iterator();
		for (int positionX = 0; positionX < size; positionX++) {
			KnowledgeElement targetElement = iterator.next().getElement();
			if (targetElement.getId() == sourceElement.getId()) {
				row[positionX] = null;
			} else {
				Link linkToTargetElement = filteredGraph.getEdge(sourceElement, targetElement);
				row[positionX] = linkToTargetElement;
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
	@XmlElement
	public Map<String, String> getLinkTypesWithColor() {
		Map<String, String> linkTypesWithColor = new TreeMap<>();
		for (String linkTypeName : DecisionKnowledgeProject.getNamesOfLinkTypes()) {
			String color = LinkType.getLinkTypeColor(linkTypeName);
			linkTypesWithColor.put(linkTypeName, color);
		}
		return linkTypesWithColor;
	}
}