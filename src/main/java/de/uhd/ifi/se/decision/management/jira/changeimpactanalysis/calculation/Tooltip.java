package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.calculation;

import java.util.Map;

import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.KnowledgeElementWithImpact;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;

/**
 * Generates a String containing a tooltip explaining the Change Impact Analysis Calculation.
 * 
 * @see ChangeImpactAnalysisService
 */
public class Tooltip {
    public static String createTooltip(KnowledgeElementWithImpact element, FilterSettings filterSettings) {
        // Root node
        if (filterSettings.getSelectedElement().getId() == element.getId()) {
            return "This is the source node from which the Change Impact Analysis was calculated.";
        }

        // Propagation Rules 
        String propagationRuleSummary = "";
        for(Map.Entry<String, Double> entry : element.getPropagationRules().entrySet()) {
                propagationRuleSummary = propagationRuleSummary + "-> " + String
                    .format("%.2f", entry.getValue()) + ": " + entry.getKey() + "\n";
        }
        if (!propagationRuleSummary.equals("")) {
            propagationRuleSummary = "\nPropagation Rule Value: " + String.format("%.2f", element.getRuleBasedValue()) +
            "; Utilized Rules:\n" + propagationRuleSummary;
        }

        // Tooltip Construction
        String tooltip = "Overall CIA Impact Factor: " + String.format("%.2f", element.getImpactValue()) +
        "\n--- --- --- --- --- --- --- --- ---" +
        "\nParent Node Impact: " + String.format("%.2f", element.getParentImpact()) +
        "\nLink Type Weight: " + String.format("%.2f", element.getLinkTypeWeight()) +
        propagationRuleSummary +
        "\n--- --- --- --- --- --- --- --- ---" +
        "\n" + element.getImpactExplanation();

        return tooltip;
    }
}
