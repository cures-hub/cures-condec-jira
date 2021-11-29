package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.calculation.Calculator;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.calculation.Colorizer;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.FilteringManager;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.view.matrix.Matrix;
import de.uhd.ifi.se.decision.management.jira.view.treeviewer.TreeViewer;
import de.uhd.ifi.se.decision.management.jira.view.vis.VisGraph;

/**
 * Responsible for change impact highlighting in the graph, tree, and matrix
 * views. During change impact analysis, each {@link KnowledgeElement}
 * (node/vertex) in the {@link KnowledgeGraph} is given an impact value. High
 * impact values indicate that the element is highly affected by the change and
 * needs to be changed as well. The impact value of an element (elementImpact)
 * is calculated using the following equation:
 * 
 * <b>elementImpact = parentImpact * (1 - decayValue) * linkTypeWeight *
 * ruleBasedValue</b>
 * 
 * where parentImpact is the element impact of the ancestor node in the
 * knowledge graph, decayValue is the decay per iteration step, linkTypeWeight
 * is a link type specific decay value between 0 and 1 of the traversed edge
 * between the parent/ancestor element and the current element, and
 * ruleBasedValue is calculated based on {@link ChangePropagationRule}s.
 */
public class ChangeImpactAnalysisService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ChangeImpactAnalysisService.class);

	public static TreeViewer calculateTreeImpact(FilterSettings filterSettings) {
		List<KnowledgeElementWithImpact> impactedElements = calculateImpactedKnowledgeElements(filterSettings);
		TreeViewer tree = new TreeViewer(filterSettings);
		tree.getNodes().forEach(node -> {
			Colorizer.colorizeTreeNode(node, impactedElements, filterSettings);
		});
		return tree;
	}

	public static VisGraph calculateGraphImpact(FilterSettings filterSettings) {
		List<KnowledgeElementWithImpact> impactedElements = calculateImpactedKnowledgeElements(filterSettings);
		KnowledgeGraph graph = new FilteringManager(filterSettings).getFilteredGraph();
		return asVisGraph(impactedElements, filterSettings, graph);
	}

	public static Matrix calculateMatrixImpact(FilterSettings filterSettings) {
		List<KnowledgeElementWithImpact> impactedElements = calculateImpactedKnowledgeElements(filterSettings);
		FilteringManager filteringManager = new FilteringManager(filterSettings);
		Set<KnowledgeElementWithImpact> elementSet = impactedElements.stream()
			.filter(element -> filteringManager.isElementMatchingKnowledgeTypeFilter(element.getElement()))
			.filter(element -> filteringManager.isElementMatchingStatusFilter(element.getElement()))
			.collect(Collectors.toSet());
				
		Map<KnowledgeElementWithImpact, String> colorMap = new HashMap<>();
		elementSet.forEach(entry -> {
			if (impactedElements.get(impactedElements.indexOf(entry)).getImpactValue() != 0.0) {
				colorMap.put(entry, Colorizer.colorForImpact(impactedElements.get(impactedElements.indexOf(entry)).getImpactValue()));
			} else {
				colorMap.put(entry, "#FFFFFF");
			}
		});
		return new Matrix(filterSettings, colorMap);
	}
	
	private static List<KnowledgeElementWithImpact> calculateImpactedKnowledgeElements(FilterSettings filterSettings) {
		List<KnowledgeElementWithImpact> impactedElements = new ArrayList<>();
		KnowledgeElementWithImpact rootElement = new KnowledgeElementWithImpact(filterSettings.getSelectedElement());
		impactedElements.add(rootElement);
		impactedElements = Calculator.calculateChangeImpact(rootElement, 1.0, filterSettings, impactedElements,
				(long) filterSettings.getLinkDistance());
		LOGGER.info("ConDec change impact analysis estimated {} impacted elements.", impactedElements.size());
		return impactedElements;
	}

	private static VisGraph asVisGraph(List<KnowledgeElementWithImpact> impactedElements, FilterSettings filterSettings,
			KnowledgeGraph graph) {
		VisGraph graphVis = new VisGraph(filterSettings);
		graphVis.getNodes().forEach(node -> {
			impactedElements.stream().forEach( element -> {
				if (element.getElement() != node.getElement()) {
					Colorizer.colorizeVisNode(node, 0.0);
				} else {
					Colorizer.colorizeVisNode(node, element.getImpactValue());
				}
			});
		});
		return graphVis;
	}
}
