package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.calculation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.ChangeImpactAnalysisConfiguration;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.ChangeImpactAnalysisService;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.ChangePropagationRule;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.KnowledgeElementWithImpact;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Link;

/**
 * Calculates the change impact scores of a {@link KnowledgeElementWithImpact}.
 * Creates a list of {@link KnowledgeElementWithImpact} containing the calculated scores as output.
 * 
 * @see ChangeImpactAnalysisService
 * @see ChangePropagationRule
 */
public class Calculator {

    private static final Logger LOGGER = LoggerFactory.getLogger(Calculator.class);

    public static List<KnowledgeElementWithImpact> calculateChangeImpact(
        KnowledgeElement currentElement, double parentImpact,
        FilterSettings filterSettings, List<KnowledgeElementWithImpact> impactedElements, long context) {
        ChangeImpactAnalysisConfiguration ciaConfig = filterSettings.getChangeImpactAnalysisConfig();
		
		// Iterating through all outgoing and incoming links of the current element
		for (Link link : currentElement.getLinks()) {
			boolean isOutwardLink = link.isOutwardLinkFrom(currentElement);
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
				ruleBasedValue *= rule.getFunction().isChangePropagated(filterSettings, currentElement, link);

				// Each rule is individually mapped with its description and impact score
				mapOfRules.put(
					rule.getDescription(),
					rule.getFunction().isChangePropagated(filterSettings, currentElement, link)
				);
			}
			double impactValue = parentImpact * linkTypeWeight * (1 - decayValue) * ruleBasedValue;

			/*
				Set the impact explanation by checking the minimum of each calculated impact score.
				The minimum impacts the score the most, therefore the explanation aims to give a textual reason why.
			*/
			String impactExplanation = "";
			if (Math.min(parentImpact, Math.min(linkTypeWeight, ruleBasedValue)) == parentImpact
				&& ((1 - parentImpact) >= decayValue)) {
					impactExplanation =  "This element has a lowered chance of being affected" +
					" by a change introduced in the source node, mainly due to its parent having a lowered impact score.\n";
			} else if (Math.min(linkTypeWeight, Math.min(parentImpact, ruleBasedValue)) == linkTypeWeight
				&& ((1 - linkTypeWeight) >= decayValue)) {
					impactExplanation = "This element has a lowered chance of being affected" +
					" by a change introduced in the source node, mainly due to the link type of the traversed edge" +
					" between this node and its parent.\n";
			} else if (Math.min(ruleBasedValue, Math.min(parentImpact, linkTypeWeight)) == ruleBasedValue
				&& ((1 - ruleBasedValue) >= decayValue)) {
					impactExplanation = "This element has a lowered chance of being affected" +
					" by a change introduced in the source node, mainly due to a used propagation rule.\n";
			} else {
				impactExplanation = "This element has a lowered chance of being affected" +
				" by a change introduced in the source node, mainly due to the decay value.\n";
			}
			if (impactValue >= 0.5) {
				impactExplanation += "A high impact value generally indicates that the element is highly affected " +
				"by the change and might need to be changed as well.\n";
			} else {
				impactExplanation += "A low impact value generally indicates that the element is less likely to be affected " +
				"by the change and probably doesn't need to be changed as well.\n";
			}

			// Add calculated impact values to new KnowledgeElementWithImpact
			KnowledgeElementWithImpact nextElement = (isOutwardLink)
				? new KnowledgeElementWithImpact(link.getTarget(),
					impactValue, parentImpact, linkTypeWeight, ruleBasedValue, mapOfRules, impactExplanation)
				: new KnowledgeElementWithImpact(link.getSource(),
					impactValue, parentImpact, linkTypeWeight, ruleBasedValue, mapOfRules, impactExplanation);

			// Determine the next element in the path
			KnowledgeElement nextElementInPath = (isOutwardLink)
				? link.getTarget()
				: link.getSource();

			// Check whether element should be added to list of impacted elements
			if (impactValue >= ciaConfig.getThreshold()) {
				if (!impactedElements.contains(nextElement)) {
					impactedElements.add(nextElement);
					calculateChangeImpact(nextElementInPath, impactValue, filterSettings, impactedElements, context);
				} else if (impactedElements.get(impactedElements.indexOf(nextElement)).getImpactValue() < impactValue) {
					impactedElements.set(impactedElements.indexOf(nextElement), nextElement);
					calculateChangeImpact(nextElementInPath, impactValue, filterSettings, impactedElements, context);
				}
			} else if (ciaConfig.getContext() > 0 && context > 0 && !impactedElements.contains(nextElement)) {
				impactedElements.add(nextElement);
				calculateChangeImpact(nextElementInPath, 0.0, filterSettings, impactedElements, context - 1);
			}
		}
		return impactedElements;
    }
}
