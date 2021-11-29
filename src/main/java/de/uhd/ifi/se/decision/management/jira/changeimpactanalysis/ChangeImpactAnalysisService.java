package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
			colorizeNode(node, impactedElements);
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
				colorMap.put(entry, colorForImpact(impactedElements.get(impactedElements.indexOf(entry)).getImpactValue()));
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
		calculateImpactedKnowledgeElementsHelper(rootElement, 1.0, filterSettings, impactedElements,
				(long) filterSettings.getLinkDistance());
		LOGGER.info("ConDec change impact analysis estimated {} impacted elements.", impactedElements.size());
		return impactedElements;
	}

	private static void calculateImpactedKnowledgeElementsHelper(KnowledgeElementWithImpact currentElement, double parentImpact,
			FilterSettings filterSettings, List<KnowledgeElementWithImpact> impactedElements, long context) {
		ChangeImpactAnalysisConfiguration ciaConfig = filterSettings.getChangeImpactAnalysisConfig();

		for (Link link : currentElement.getElement().getLinks()) {
			boolean isOutwardLink = link.isOutwardLinkFrom(currentElement.getElement());
			String linkTypeName = (isOutwardLink)
				? link.getType().getOutwardName()
				: link.getType().getInwardName();

			if (!ciaConfig.getLinkImpact().containsKey(linkTypeName)) {
				LOGGER.warn("CIA couldn't be processed: {}", "link -> " + linkTypeName + ", source -> "
						+ link.getSource().getId() + ", target -> " + link.getTarget().getId());
			}

			// Calculate distinct impact values
			double linkTypeWeight = ciaConfig.getLinkImpact().getOrDefault(linkTypeName, 1.0f);
			double decayValue = ciaConfig.getDecayValue();
			double ruleBasedValue = 1.0;
			Map<String, Double> mapOfRules = new HashMap<>();
			for (ChangePropagationRule rule : ciaConfig.getPropagationRules()) {
				ruleBasedValue *= rule.getFunction().isChangePropagated(filterSettings, currentElement.getElement(), link);

				// Each rule is individually mapped with its description and impact score
				mapOfRules.put(
					rule.getDescription(),
					rule.getFunction().isChangePropagated(filterSettings, currentElement.getElement(), link)
				);
			}
			double impactValue = parentImpact * linkTypeWeight * (1 - decayValue) * ruleBasedValue;

			String impactExplanation = "";
			if (Math.min(parentImpact, Math.min(linkTypeWeight, ruleBasedValue)) == parentImpact
				&& ((1 - parentImpact) >= decayValue)) {
					impactExplanation =  "This element has a lowered chance of being affected" +
					" by a change introduced in the source node, mainly due to its parent having a low impact score. ";
			} else if (Math.min(linkTypeWeight, Math.min(parentImpact, ruleBasedValue)) == linkTypeWeight
				&& ((1 - linkTypeWeight) >= decayValue)) {
					impactExplanation = "This element has a lowered chance of being affected" +
					" by a change introduced in the source node, mainly due to the link type of the traversed edge" +
					" between this node and its parent. ";
			} else if (Math.min(ruleBasedValue, Math.min(parentImpact, linkTypeWeight)) == ruleBasedValue
				&& ((1 - ruleBasedValue) >= decayValue)) {
					impactExplanation = "This element has a lowered chance of being affected" +
					" by a change introduced in the source node, mainly due to a used propagation rule. ";
			} else {
				impactExplanation = "This element has a lowered chance of being affected" +
				" by a change introduced in the source node, mainly due to the decay value. ";
			}
			impactExplanation += "A high impact value generally indicates that the element is highly affected " +
				"by the change and might need to be changed as well.";

			// Add calculated impact values to knowledge element
			KnowledgeElementWithImpact nextElement = (isOutwardLink)
				? new KnowledgeElementWithImpact(link.getTarget(),
					impactValue, parentImpact, linkTypeWeight, ruleBasedValue, mapOfRules, impactExplanation)
				: new KnowledgeElementWithImpact(link.getSource(),
					impactValue, parentImpact, linkTypeWeight, ruleBasedValue, mapOfRules, impactExplanation);

			// Check whether element should be added to list of impacted elements
			if (impactValue >= ciaConfig.getThreshold()) {
				if (!impactedElements.contains(nextElement)) {
					impactedElements.add(nextElement);
					calculateImpactedKnowledgeElementsHelper(nextElement, impactValue, filterSettings, impactedElements, context);
				} else if (impactedElements.get(impactedElements.indexOf(nextElement)).getImpactValue() < impactValue) {
					impactedElements.set(impactedElements.indexOf(nextElement), nextElement);
					calculateImpactedKnowledgeElementsHelper(nextElement, impactValue, filterSettings, impactedElements, context);
				}
			} else if (ciaConfig.getContext() > 0 && context > 0 && !impactedElements.contains(nextElement)) {
				impactedElements.add(nextElement);
				calculateImpactedKnowledgeElementsHelper(nextElement, 0.0, filterSettings, impactedElements, context - 1);
			}
		}
	}

	private static VisGraph asVisGraph(List<KnowledgeElementWithImpact> impactedElements, FilterSettings filterSettings,
			KnowledgeGraph graph) {
		VisGraph graphVis = new VisGraph(filterSettings);
		graphVis.getNodes().forEach(node -> {
			impactedElements.stream().forEach( element -> {
				if (element.getElement() != node.getElement()) {
					colorizeNode(node, 0.0);
				} else {
					colorizeNode(node, element.getImpactValue());
				}
			});
		});
		return graphVis;
	}

	private static void colorizeNode(TreeViewerNode node, List<KnowledgeElementWithImpact> impactedElements) {
		String style = "";
		KnowledgeElementWithImpact treeViewerNode = new KnowledgeElementWithImpact(node.getElement());
		String propagationRuleSummary = "";
		String clzz = node.getLiAttr().get("class");		

		if (impactedElements.contains(treeViewerNode)) {
			style = "background-color:" + colorForImpact(impactedElements
				.get(impactedElements.indexOf(treeViewerNode)).getImpactValue());
			/*
				Iterating through all utilized propagation rules,
				appending each to a single summarizing String
			*/
			for(Map.Entry<String, Double> entry : impactedElements.get(
				impactedElements.indexOf(treeViewerNode)).getPropagationRules().entrySet()) {
					propagationRuleSummary = propagationRuleSummary + "-> " + String
						.format("%.2f", entry.getValue()) + ": " + entry.getKey() + "\n";
			}
			node.setLiAttr(ImmutableMap.<String, String>builder()
				.put("style", style)
				.put("class", clzz)
				.put("cia_parentImpact",
					String.format("%.2f", impactedElements
					.get(impactedElements.indexOf(treeViewerNode)).getParentImpact()))
				.put("cia_linkTypeWeight",
					String.format("%.2f", impactedElements
					.get(impactedElements.indexOf(treeViewerNode)).getLinkTypeWeight()))
				.put("cia_ruleBasedValue",
					String.format("%.2f", impactedElements
					.get(impactedElements.indexOf(treeViewerNode)).getRuleBasedValue()))
				.put("cia_impactFactor",
					String.format("%.2f", impactedElements
					.get(impactedElements.indexOf(treeViewerNode)).getImpactValue()))
				.put("cia_propagationRuleSummary", propagationRuleSummary)
				.put("cia_valueExplanation", impactedElements
					.get(impactedElements.indexOf(treeViewerNode)).getImpactExplanation())
				.build());
		} else {
			style = "background-color:white";
			node.setLiAttr(ImmutableMap.<String, String>builder()
				.put("style", style)
				.put("class", clzz)
				.build());
		}

		String aStyle = "color:black";
		node.setAttr(ImmutableMap.of("style", aStyle));
		node.getChildren().forEach(child -> {
			colorizeNode(child, impactedElements);
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
