package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.calculation;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.KnowledgeElementWithImpact;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.view.matrix.MatrixNode;
import de.uhd.ifi.se.decision.management.jira.view.treeviewer.TreeViewerNode;
import de.uhd.ifi.se.decision.management.jira.view.vis.VisNode;

/**
 * Colorizes an object based on their change impact score.
 * 
 * @see Calculator
 */
public class Colorizer {

    // Background color for the root node
    private static String rootColor = "#FFFFFF";
    // Background color for nodes which are included due to the context setting
    private static String contextColor = "#ce93d8";
    // Background color for link recommendation nodes
    private static String recommendationColor = "#90caf9";

    public static TreeViewerNode colorizeTreeNode(List<KnowledgeElementWithImpact> impactedElements, TreeViewerNode node, FilterSettings filterSettings) {
        String style = "";
        String clzz = node.getLiAttr().get("class");

        for (KnowledgeElementWithImpact element : impactedElements) {
            if (node.getElement().getId() == element.getId()) {
                // Painting the background color white for the root node to prevent a red
                // background due to root impactValue always being 1.0
                if (filterSettings.getSelectedElement().getId() == element.getId()) {
                    style = "background-color:" + rootColor;
                // Using a designated color to mark elements which are below the threshold but
                // have been included due to the context setting being larger than 0
                } else  if (element.getImpactExplanation().contains("context")) {
                    style = "background-color:" + contextColor;
                } else {
                    style = "background-color:" + colorForImpact(element.getImpactValue());
                }

                node.setLiAttr(ImmutableMap.<String, String>builder()
                    .put("style", style)
                    .put("class", clzz)
                    .put("cia_tooltip", Tooltip.createTooltip(element, filterSettings))
                    .build());

                String aStyle = "color:black";
                node.setAttr(ImmutableMap.of("style", aStyle));
                node.getChildren().forEach(child -> {
                    child = colorizeTreeNode(impactedElements, child, filterSettings);
                });
                break;
            }
        }
        // Colorization for link recommendation nodes that have no CIA score
        if (!node.getLiAttr().containsKey("cia_tooltip")) {
            node.setLiAttr(ImmutableMap.<String, String>builder()
                    .put("style", "background-color:" + recommendationColor)
                    .put("class", node.getLiAttr().get("class"))
                    .put("cia_tooltip", Tooltip.createLinkRecommendationTooltip())
                    .build());

            String aStyle = "color:black";
            node.setAttr(ImmutableMap.of("style", aStyle));
        }
        return node;
    }

    public static VisNode colorizeVisNode(List<KnowledgeElementWithImpact> impactedElements, VisNode node, FilterSettings filterSettings) {
        Map<String, String> colorMap = new HashMap<>();
        Map<String, String> fontMap = new HashMap<>();
        for (KnowledgeElementWithImpact element : impactedElements) {
            if (node.getElement().getId() == element.getId()) {
                // Painting the background color white for the root node to prevent a red
                // background due to root impactValue always being 1.0
                if (filterSettings.getSelectedElement().getId() == element.getId()) {
                    colorMap.put("background", rootColor);
                // Using a designated color to mark elements which are below the threshold but
                // have been included due to the context setting being larger than 0
                } else if (element.getImpactExplanation().contains("context")) {
                    colorMap.put("background", contextColor);
                } else {
                    colorMap.put("background", colorForImpact(element.getImpactValue()));
                }
                colorMap.put("border", "black");
                fontMap.put("color", "black");
                node.setFont(fontMap);
                node.getColorMap().putAll(colorMap);
                node.setTitle(Tooltip.createTooltip(element, filterSettings));
            }
        }
        // Colorization for link recommendation nodes that have no CIA score
        if (!node.getTitle().contains("CIA") && !node.getTitle().contains("Change Impact Analysis")) {
            colorMap.put("background", recommendationColor);
            fontMap.put("color", "black");
            node.setFont(fontMap);
            node.getColorMap().putAll(colorMap);
            node.setTitle(Tooltip.createLinkRecommendationTooltip());
        }
        return node;
    }

    public static MatrixNode colorizeMatrixNode(List<KnowledgeElementWithImpact> impactedElements, MatrixNode node, FilterSettings filterSettings) {
        for (KnowledgeElementWithImpact element : impactedElements) {
            if (node.getElement().getId() == element.getId()) {
                // Painting the background color white for the root node to prevent a red
                // background due to root impactValue always being 1.0
                if (filterSettings.getSelectedElement().getId() == element.getId()) {
                    node.setChangeImpactColor(rootColor);
                // Using a designated color to mark elements which are below the threshold but
                // have been included due to the context setting being larger than 0
                } else if (element.getImpactExplanation().contains("context")) {
                    node.setChangeImpactColor(contextColor);
                } else {
                    node.setChangeImpactColor(colorForImpact(element.getImpactValue()));
                }
                node.setChangeImpactExplanation(Tooltip.createTooltip(element, filterSettings));
            }
        }
        // Colorization for link recommendation nodes that have no CIA score
        if (!node.getChangeImpactExplanation().contains("CIA") && !node.getChangeImpactExplanation().contains("Change Impact Analysis")) {
            node.setChangeImpactColor(recommendationColor);
            node.setChangeImpactExplanation(Tooltip.createLinkRecommendationTooltip());
        }
        return node;
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
