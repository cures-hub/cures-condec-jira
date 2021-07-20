package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.FilteringManager;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.view.matrix.Matrix;
import de.uhd.ifi.se.decision.management.jira.view.treeviewer.TreeViewer;
import de.uhd.ifi.se.decision.management.jira.view.treeviewer.TreeViewerNode;
import de.uhd.ifi.se.decision.management.jira.view.vis.VisGraph;
import de.uhd.ifi.se.decision.management.jira.view.vis.VisNode;

/**
 * Responsible for change impact highlighting in the graph, tree, and matrix
 * views. During change impact analysis, each {@link KnowledgeElement}
 * (node/vertex) in the {@link KnowledgeGraph} is given an impact value. High
 * impact values indicate that the element is highly affected by the change and
 * needs to be changed as well. The impact value of an element (elementImpact)
 * is calculated using the following equation:
 * 
 * <b>elementImpact = parentImpact * decayValue * linkTypeWeight *
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
		Map<KnowledgeElement, Double> results = calculateImpactedKnowledgeElements(filterSettings);
		TreeViewer tree = new TreeViewer(filterSettings);
		tree.getNodes().forEach(node -> {
			colorizeNode(node, results);
		});
		return tree;
	}

	public static VisGraph calculateGraphImpact(FilterSettings filterSettings) {
		Map<KnowledgeElement, Double> results = calculateImpactedKnowledgeElements(filterSettings);
		KnowledgeGraph graph = new FilteringManager(filterSettings).getFilteredGraph();
		return asVisGraph(results, filterSettings, graph);
	}

	public static Matrix calculateMatrixImpact(FilterSettings filterSettings) {
		Map<KnowledgeElement, Double> impactGraph = calculateImpactedKnowledgeElements(filterSettings);
		FilteringManager filteringManager = new FilteringManager(filterSettings);
		Set<KnowledgeElement> elementSet = impactGraph.keySet().stream()
				.filter(knowledgeElement -> filteringManager.isElementMatchingKnowledgeTypeFilter(knowledgeElement))
				.filter(knowledgeElement -> filteringManager.isElementMatchingStatusFilter(knowledgeElement))
				.collect(Collectors.toSet());
		Map<KnowledgeElement, String> colorMap = new HashMap<>();
		elementSet.forEach(entry -> {
			if (impactGraph.get(entry) != 0.0) {
				colorMap.put(entry, colorForImpact(impactGraph.get(entry)));
			} else {
				colorMap.put(entry, "#FFFFFF");
			}
		});
		return new Matrix(filterSettings, colorMap);
	}

	private static Map<KnowledgeElement, Double> calculateImpactedKnowledgeElements(FilterSettings filterSettings) {
		Map<KnowledgeElement, Double> results = new HashMap<>();
		KnowledgeElement root = filterSettings.getSelectedElement();
		results.put(root, 1.0);
		calculateImpactedKnowledgeElementsHelper(root, 1.0, filterSettings, results,
				(long) filterSettings.getDefinitionOfDone().getMaximumLinkDistanceToDecisions());
		LOGGER.info("ConDec change impact analysis estimated {} impacted elements.", results.size());
		return results;
	}

	private static void calculateImpactedKnowledgeElementsHelper(KnowledgeElement currentElement, double parentImpact,
			FilterSettings filterSettings, Map<KnowledgeElement, Double> results, long context) {
		ChangeImpactAnalysisConfiguration ciaConfig = filterSettings.getChangeImpactAnalysisConfig();
		for (Link link : currentElement.getLinks()) {
			boolean isOutwardLink = link.isOutwardLinkFrom(currentElement);
			String linkTypeName = (isOutwardLink) ? link.getType().getOutwardName() : link.getType().getInwardName();
			if (!ciaConfig.getLinkImpact().containsKey(linkTypeName)) {
				LOGGER.warn("CIA couldn't be processed: {}", "link -> " + linkTypeName + ", source -> "
						+ link.getSource().getId() + ", target -> " + link.getTarget().getId());
			}
			double linkTypeWeight = ciaConfig.getLinkImpact().getOrDefault(linkTypeName, 1.0f);
			double decayValue = ciaConfig.getDecayValue();

			double ruleBasedValue = 1.0;
			for (ChangePropagationRule rule : ciaConfig.getPropagationRules()) {
				ruleBasedValue *= rule.getFunction().isChangePropagated(filterSettings, currentElement, link);
			}

			double impact = parentImpact * linkTypeWeight * decayValue * ruleBasedValue;

			KnowledgeElement nextElement = (isOutwardLink) ? link.getTarget() : link.getSource();

			if (impact >= ciaConfig.getThreshold()) {
				if (!results.containsKey(nextElement) || results.get(nextElement) < impact) {
					results.put(nextElement, impact);
					calculateImpactedKnowledgeElementsHelper(nextElement, impact, filterSettings, results, context);
				}
			} else if (ciaConfig.getContext() > 0 && context > 0 && !results.containsKey(nextElement)) {
				results.put(nextElement, 0.0);
				calculateImpactedKnowledgeElementsHelper(nextElement, 0.0, filterSettings, results, context - 1);
			}
		}
	}

	private static VisGraph asVisGraph(Map<KnowledgeElement, Double> results, FilterSettings filterSettings,
			KnowledgeGraph graph) {
		VisGraph graphVis = new VisGraph(filterSettings);
		graphVis.getNodes().forEach(node -> colorizeNode(node, results.getOrDefault(node.getElement(), 0.0)));
		return graphVis;
	}

	private static void colorizeNode(TreeViewerNode node, Map<KnowledgeElement, Double> results) {
		String style = "";
		if (results.containsKey(node.getElement())) {
			style = "background-color:" + colorForImpact(results.get(node.getElement()));
		} else {
			style = "background-color:white";
		}
		String clzz = node.getLiAttr().get("class");
		node.setLiAttr(ImmutableMap.<String, String>builder().put("style", style).put("class", clzz).build());
		String aStyle = "color:black";
		node.setAttr(ImmutableMap.of("style", aStyle));
		node.getChildren().forEach(child -> {
			colorizeNode(child, results);
		});
	}

	private static void colorizeNode(VisNode node, double impact) {
		Map<String, String> colorMap = new HashMap<>();
		colorMap.put("background", colorForImpact(impact));
		colorMap.put("border", "black");
		node.getColorMap().putAll(colorMap);
		if (impact <= 0) {
			node.setCollapsed();
		}
	}

	private static String colorForImpact(double impact) {
		Color red = Color.RED;
		Color green = Color.GREEN;
		Color blendColor = blend(green, red, (float) impact);
		return String.format("#%02x%02x%02x", blendColor.getRed(), blendColor.getGreen(), blendColor.getBlue());
	}

	private static Color blend(Color color1, Color color2, float pRatio) {
		float ratio = pRatio;
		if (ratio > 1f)
			ratio = 1f;
		else if (ratio < 0f)
			ratio = 0f;
		float iRatio = 1.0f - ratio;

		int i1 = color1.getRGB();
		int i2 = color2.getRGB();

		int a1 = (i1 >> 24 & 0xff);
		int r1 = ((i1 & 0xff0000) >> 16);
		int g1 = ((i1 & 0xff00) >> 8);
		int b1 = (i1 & 0xff);

		int a2 = (i2 >> 24 & 0xff);
		int r2 = ((i2 & 0xff0000) >> 16);
		int g2 = ((i2 & 0xff00) >> 8);
		int b2 = (i2 & 0xff);

		int a = (int) (a1 * iRatio + a2 * ratio);
		int r = (int) (r1 * iRatio + r2 * ratio);
		int g = (int) (g1 * iRatio + g2 * ratio);
		int b = (int) (b1 * iRatio + b2 * ratio);

		return new Color(a << 24 | r << 16 | g << 8 | b);
	}
}
