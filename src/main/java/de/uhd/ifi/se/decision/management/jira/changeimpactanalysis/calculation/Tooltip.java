package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.calculation;

import java.util.Map;

import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.KnowledgeElementWithImpact;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.LinkType;

/**
 * Generates a String containing a tooltip explaining the Change Impact Analysis Calculation.
 * 
 * @see ChangeImpactAnalysisService
 */
public class Tooltip {
    public static String createTooltip(KnowledgeElementWithImpact element, FilterSettings filterSettings) {
        // Root node
        if (filterSettings.getSelectedElement().getId() == element.getId()) {
            return "This is the source element from which the Change Impact Analysis was calculated.";
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

    /**
	 * Generates the tooltip for link recommendations.
	 *  
	 * @return String containing the link recommendation tooltip
	 */
    public static String createLinkRecommendationTooltip() {
        return "This element is not implicitly linked to the source element but has been included as a result of the link recommendation.";
    }

    /**
	 * Generates the impact explanation by checking the minimum of each calculated
	 * impact score. The minimum impacts the score the most, therefore the
	 * explanation aims to give a textual reason why.
	 *  
	 * @return String containing the impact value explanation
	 */
	public static String generateImpactExplanation(double parentImpact, double ruleBasedValue, double decayValue,
            double impactValue, String linkTypeName, double linkRecommendationScore) {
        String impactExplanation = "";
        if (Math.min(parentImpact, ruleBasedValue) == parentImpact && ((1 - parentImpact) >= decayValue)) {
            impactExplanation = "This element has a lowered chance of being affected"
                    + " by a change introduced in the source node, mainly due to its parent having a lowered impact score.\n";
        } else if ((1 - ruleBasedValue) >= decayValue) {
            impactExplanation = "This element has a lowered chance of being affected"
                    + " by a change introduced in the source node, mainly due to a used propagation rule.\n";
        } else {
            impactExplanation = "This element has a lowered chance of being affected"
                    + " by a change introduced in the source node, mainly due to the decay value.\n";
        }
        if (impactValue >= 0.5) {
            impactExplanation += "A high impact value generally indicates that the element is highly affected "
                    + "by the change and might need to be changed as well.\n";
        } else {
            impactExplanation += "A low impact value generally indicates that the element is less likely to be affected "
                    + "by the change and probably doesn't need to be changed as well.\n";
        }
        if (linkTypeName.equalsIgnoreCase(LinkType.RECOMMENDED.getName())) {
            impactExplanation += "\n--- --- --- --- --- --- --- --- ---"
                    + "\nThis element is not implicitly linked to the source element but has been "
                    + "included as a result of the link recommendation.\nLink Recommendation Score: "
                    + String.format("%.2f", linkRecommendationScore);
        }
        return impactExplanation;
    }
}
