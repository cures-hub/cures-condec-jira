package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.calculation;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.KnowledgeElementWithImpact;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.view.treeviewer.TreeViewerNode;
import de.uhd.ifi.se.decision.management.jira.view.vis.VisNode;

/**
 * Colorizes {@link TreeViewerNode} based on their change impact scores.
 * 
 * @see Calculator
 */
public class Colorizer {
    public static void colorizeTreeNode(TreeViewerNode node,
        List<KnowledgeElementWithImpact> impactedElements, FilterSettings filterSettings) {
        String style = "";
        KnowledgeElementWithImpact treeViewerNode = new KnowledgeElementWithImpact(node.getElement());
        String propagationRuleSummary = "";
        String clzz = node.getLiAttr().get("class");

        if (impactedElements.contains(treeViewerNode)) {
            /*
                Painting the background color white for the root node to prevent a red
                background due to root impactValue always being 1.0
            */
            if (filterSettings.getSelectedElement() == treeViewerNode.getElement()) {
                style = "background-color:white";
            } else {
                style = "background-color:" + colorForImpact(impactedElements
                    .get(impactedElements.indexOf(treeViewerNode)).getImpactValue());
            }
            /*
                Iterating through all utilized propagation rules,
                appending each to a single summarizing String
            */
            for(Map.Entry<String, Double> entry : impactedElements.get(
                impactedElements.indexOf(treeViewerNode)).getPropagationRules().entrySet()) {
                    propagationRuleSummary = propagationRuleSummary + "-> " + String
                        .format("%.2f", entry.getValue()) + ": " + entry.getKey() + "\n";
            }
            /*
                Saving the Li attributes inside the node, containing all previously
                determined CIA impact scores and the corresponding explanation
            */
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
            colorizeTreeNode(child, impactedElements, filterSettings);
        });
    }

    public static void colorizeVisNode(VisNode node, double impact) {
        Map<String, String> colorMap = new HashMap<>();
        colorMap.put("background", colorForImpact(impact));
        colorMap.put("border", "black");
        node.getColorMap().putAll(colorMap);
        if (impact <= 0) {
            node.setCollapsed();
        }
    }

    public static String colorForImpact(double impact) {
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
