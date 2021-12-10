package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.calculation.Calculator;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.calculation.Colorizer;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
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
		// Calculate impacted elements
		List<KnowledgeElementWithImpact> impactedElements = calculateImpactedKnowledgeElements(filterSettings);

		// Construct JSTree based on impacted elements
		TreeViewer tree = new TreeViewer(filterSettings, impactedElements);
		
		// Colorize individual nodes
		tree.getNodes().forEach(node -> {
			node = Colorizer.colorizeTreeNode(impactedElements, node, filterSettings);
			}
		);
		return tree;
	}

	public static VisGraph calculateGraphImpact(FilterSettings filterSettings) {
		// Calculate impacted elements
		List<KnowledgeElementWithImpact> impactedElements = calculateImpactedKnowledgeElements(filterSettings);

		// Construct VisGraph based on impacted elements
		VisGraph graphVis = new VisGraph(filterSettings, impactedElements);
		
		// Colorize individual nodes
		graphVis.getNodes().forEach(node -> {
			for (KnowledgeElementWithImpact element : impactedElements) {
				if (node.getElement().getId() == element.getId()) {
					node = Colorizer.colorizeVisNode(element, node, filterSettings);
					break;
				}
			}
		});
		return graphVis;
	}

	public static Matrix calculateMatrixImpact(FilterSettings filterSettings) {
		// Calculate impacted elements
		List<KnowledgeElementWithImpact> impactedElements = calculateImpactedKnowledgeElements(filterSettings);

		// Construct Matrix based on impacted elements
		Matrix matrix = new Matrix(filterSettings, impactedElements);
		
		// Colorize individual nodes
		matrix.getHeaderElementsWithHighlighting().forEach(node -> {
			for (KnowledgeElementWithImpact element : impactedElements) {
				if (node.getElement().getId() == element.getId()) {
					Colorizer.colorizeMatrixNode(element, node, filterSettings);
					break;
				}
			}
		});
		return matrix;
	}
	
	public static List<KnowledgeElementWithImpact> calculateImpactedKnowledgeElements(FilterSettings filterSettings) {
		List<KnowledgeElementWithImpact> impactedElements = new ArrayList<>();

		// Selected element is designated as the root of the impact calculation
		KnowledgeElementWithImpact rootElement = new KnowledgeElementWithImpact(filterSettings.getSelectedElement());
		impactedElements.add(rootElement);

		// Calculate impacted elements
		impactedElements = Calculator.calculateChangeImpact(filterSettings.getSelectedElement(), 1.0,
			filterSettings, impactedElements, (long) filterSettings.getLinkDistance());
		LOGGER.info("ConDec change impact analysis estimated {} impacted elements.", impactedElements.size());
		return impactedElements;
	}
}
