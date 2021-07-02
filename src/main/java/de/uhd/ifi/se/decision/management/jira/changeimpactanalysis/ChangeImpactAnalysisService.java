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
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.view.matrix.Matrix;
import de.uhd.ifi.se.decision.management.jira.view.treeviewer.TreeViewer;
import de.uhd.ifi.se.decision.management.jira.view.treeviewer.TreeViewerNode;
import de.uhd.ifi.se.decision.management.jira.view.vis.VisEdge;
import de.uhd.ifi.se.decision.management.jira.view.vis.VisGraph;
import de.uhd.ifi.se.decision.management.jira.view.vis.VisNode;

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
		KnowledgeGraph graph = KnowledgeGraph.getInstance(filterSettings.getProjectKey());
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
		return new Matrix(elementSet, filterSettings, colorMap);
	}

	private static Map<KnowledgeElement, Double> calculateImpactedKnowledgeElements(FilterSettings filterSettings) {
		HashMap<KnowledgeElement, Double> results = new HashMap<>();
		KnowledgeElement root = filterSettings.getSelectedElement();
		results.put(root, 1.0);
		calculateImpactedKnowledgeElementsHelper(root, 1.0, filterSettings, results,
				(long) filterSettings.getLinkDistance());
		LOGGER.info("CONDEC: {} Elements found", results.size());
		return results;
	}

	private static void calculateImpactedKnowledgeElementsHelper(KnowledgeElement root, Double parentImpact,
			FilterSettings filterSettings, Map<KnowledgeElement, Double> results, final Long context) {
		ChangeImpactAnalysisConfiguration ciaConfig = filterSettings.getChangeImpactAnalysisConfig();
		root.getLinks().forEach(link -> {
			// TODO Link specific weights
			boolean isOutwardLink = link.getSource().equals(root);
			String linkType = (isOutwardLink) ? link.getType().getOutwardName() : link.getType().getInwardName();
			if (!ciaConfig.getLinkImpact().containsKey(linkType)) {
				LOGGER.warn("CIA couldn't be processed: {}", "link -> " + linkType + ", source -> "
						+ link.getSource().getId() + ", target -> " + link.getTarget().getId());
			}
			double typeWeight = ciaConfig.getLinkImpact().getOrDefault(linkType, 1.0f);
			double decayValue = ciaConfig.getDecayValue();
			double impact = parentImpact * typeWeight * decayValue;

			KnowledgeElement next = (isOutwardLink) ? link.getTarget() : link.getSource();

			final boolean[] propagate = { true };
			ciaConfig.getPropagationRules().forEach(rule -> propagate[0] = propagate[0]
					&& rule.getPredicate().pass(root, parentImpact, next, impact, link));

			if (impact >= ciaConfig.getThreshold() && propagate[0]) {
				if (!results.containsKey(next) || (results.containsKey(next) && results.get(next) < impact)) {
					results.put(next, impact);
					calculateImpactedKnowledgeElementsHelper(next, impact, filterSettings, results, context);
				}
			} else if (ciaConfig.getContext() > 0 && context > 0 && !results.containsKey(next)) {
				results.put(next, 0.0);
				calculateImpactedKnowledgeElementsHelper(next, 0.0, filterSettings, results, context - 1);
			}
		});
	}

	private static VisGraph asVisGraph(Map<KnowledgeElement, Double> results, FilterSettings filterSettings,
			KnowledgeGraph graph) {
		Set<KnowledgeElement> nodes = results.keySet();
		VisGraph graphVis = new VisGraph();
		graphVis.setNodes(results.keySet().stream().map((entry) -> {
			boolean collapse = collapse(entry, filterSettings);
			VisNode node = new VisNode(entry, collapse, 0, filterSettings);
			if (results.get(entry) != null && results.get(entry) != 0.0) {
				colorizeNode(node, results.get(entry));
			}
			return node;
		}).collect(Collectors.toSet()));
		graph.edgeSet().stream().filter(link -> nodes.contains(link.getSource()) && nodes.contains(link.getTarget()))
				.forEach(link -> {
					VisEdge edge = new VisEdge(link);
					graphVis.getEdges().add(edge);
				});
		return graphVis;
	}

	private static boolean collapse(KnowledgeElement element, FilterSettings settings) {
		KnowledgeType type = (element.getType() == KnowledgeType.CON || element.getType() == KnowledgeType.PRO)
				? KnowledgeType.ARGUMENT
				: element.getType();
		if (element.equals(settings.getSelectedElement())) {
			return false;
		}
		return !(settings.getKnowledgeTypes().contains(type.toString()))
				|| !(settings.getStatus().contains(element.getStatus()));
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

	private static void colorizeNode(VisNode node, Double impact) {
		Map<String, String> colorMap = new HashMap<>();
		colorMap.put("background", colorForImpact(impact));
		colorMap.put("border", "black");
		node.getColorMap().putAll(colorMap);
	}

	private static String colorForImpact(Double impact) {
		Color red = Color.RED;
		Color green = Color.GREEN;
		Color blendColor = blend(green, red, impact.floatValue());
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

		int a = (int) ((a1 * iRatio) + (a2 * ratio));
		int r = (int) ((r1 * iRatio) + (r2 * ratio));
		int g = (int) ((g1 * iRatio) + (g2 * ratio));
		int b = (int) ((b1 * iRatio) + (b2 * ratio));

		return new Color(a << 24 | r << 16 | g << 8 | b);
	}
}
