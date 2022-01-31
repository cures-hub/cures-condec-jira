package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.calculation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.ChangeImpactAnalysisConfiguration;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.ChangeImpactAnalysisService;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.ChangePropagationRule;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.KnowledgeElementWithImpact;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.LinkType;
import de.uhd.ifi.se.decision.management.jira.recommendation.Recommendation;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.LinkRecommendation;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation.ContextInformation;

/**
 * Calculates the change impact scores of a {@link KnowledgeElementWithImpact}.
 * Creates a list of {@link KnowledgeElementWithImpact} containing the
 * calculated scores as output.
 * 
 * @see ChangeImpactAnalysisService
 * @see ChangePropagationRule
 */
public class Calculator {

	private static Map<String, Double> propagationRuleResult;
	private static final Logger LOGGER = LoggerFactory.getLogger(Calculator.class);

	public static List<KnowledgeElementWithImpact> calculateChangeImpact(KnowledgeElement currentElement,
			double parentImpact, FilterSettings filterSettings, List<KnowledgeElementWithImpact> impactedElements,
			long context) {
		ChangeImpactAnalysisConfiguration ciaConfig = filterSettings.getChangeImpactAnalysisConfig();
		
		// Add link recommmendations to root element if setting has been selected
		Set<Link> currentElementLinks = new HashSet<>(currentElement.getLinks());
		if (filterSettings.getSelectedElement() == currentElement
			&& filterSettings.areLinksRecommended() 
			&& filterSettings.getChangeImpactAnalysisConfig().getAreLinkRecommendationsIncludedInCalculation()) {
				ContextInformation linkRecommender = new ContextInformation(filterSettings.getSelectedElement(),
					filterSettings.getLinkRecommendationConfig());
				List<Recommendation> linkRecommendations = linkRecommender.getLinkRecommendations();
				for (Recommendation recommendation : linkRecommendations) {
					currentElementLinks.add((LinkRecommendation) recommendation);
				}
		}
		// Iterating through all outgoing and incoming links of the current element
		for (Link link : currentElementLinks) {
			String linkTypeName;
			KnowledgeElement nextElementInPath;

			// Determine next element in path
			if (link.isOutwardLinkFrom(currentElement)) {
				linkTypeName = link.getType().getOutwardName();
				nextElementInPath = link.getTarget();
			} else {
				linkTypeName = link.getType().getInwardName();
				nextElementInPath = link.getSource();
			}

			if (!ciaConfig.getLinkImpact().containsKey(linkTypeName)) {
				LOGGER.warn("CIA couldn't be processed: {}", "link -> " + linkTypeName + ", source -> "
						+ link.getSource().getId() + ", target -> " + link.getTarget().getId());
			}

			// Calculate distinct impact values
			double linkTypeWeight = ciaConfig.getLinkImpact().getOrDefault(linkTypeName, 1.0f);
			double decayValue = ciaConfig.getDecayValue();
			double ruleBasedValue = calculatePropagationRuleImpact(filterSettings, nextElementInPath, link);
			double impactValue = parentImpact * linkTypeWeight * (1 - decayValue) * ruleBasedValue;
			String impactExplanation = "";
			if (link.getClass() == LinkRecommendation.class) {
				LinkRecommendation recommendation = (LinkRecommendation) link;
				double linkRecommendationScore = recommendation.getScore().getValue() / 100;
				impactValue = impactValue * linkRecommendationScore;
				impactExplanation = generateImpactExplanation(parentImpact, ruleBasedValue,
					decayValue, impactValue, linkTypeName, linkRecommendationScore);
			} else {
				impactExplanation = generateImpactExplanation(parentImpact, ruleBasedValue,
					decayValue, impactValue, linkTypeName, 0);
			}

			// Add calculated impact values to new KnowledgeElementWithImpact
			KnowledgeElementWithImpact nextElement = new KnowledgeElementWithImpact(nextElementInPath, impactValue,
					parentImpact, linkTypeWeight, ruleBasedValue, propagationRuleResult, impactExplanation);

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
				nextElement.setImpactExplanation(
						"This element is below the set threshold but has been included due to the selected context setting.");
				impactedElements.add(nextElement);
				calculateChangeImpact(nextElementInPath, 0.0, filterSettings, impactedElements, context - 1);
			}
		}
		return impactedElements;
	}

	/**
	 * Calculates the propagation rule impact as defined by the
	 * {@link FilterSettings}.
	 * 
	 * @return Double containing the calculated propagation rule score, value
	 *         between 0 and 1.0
	 */
	public static double calculatePropagationRuleImpact(FilterSettings filterSettings, KnowledgeElement currentElement,
			Link link) {
		Map<String, Double> mapOfRules = new HashMap<>();
		double ruleBasedValue = 1.0;

		// Each rule is individually mapped with its description and corresponding
		// impact score
		for (ChangePropagationRule rule : filterSettings.getChangeImpactAnalysisConfig().getPropagationRules()) {
			if (!rule.isActive()) {
				continue;
			}
			double ruleCalculationValue = rule.getType().getFunction().isChangePropagated(filterSettings,
					currentElement, link);
			ruleBasedValue = ruleBasedValue * ruleCalculationValue;

			mapOfRules.put(rule.getType().getDescription(), ruleCalculationValue);
		}
		propagationRuleResult = mapOfRules;
		return ruleBasedValue;
	}

	/**
	 * Generates the impact explanation by checking the minimum of each calculated
	 * impact score. The minimum impacts the score the most, therefore the
	 * explanation aims to give a textual reason why.
	 * @param linkTypeName
	 * 
	 * @return String containing the the impact value explanation
	 */
	public static String generateImpactExplanation(double parentImpact, double ruleBasedValue, double decayValue,
			double impactValue, String linkTypeName, double linkRecommendationScore) {
		String impactExplanation = "";
		if (Math.min(parentImpact, ruleBasedValue) == parentImpact && ((1 - parentImpact) >= decayValue)) {
			impactExplanation = "This element has a lowered chance of being affected"
					+ " by a change introduced in the source node, mainly due to its parent having a lowered impact score.\n";
		} else if (Math.min(ruleBasedValue, parentImpact) == ruleBasedValue && ((1 - ruleBasedValue) >= decayValue)) {
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
