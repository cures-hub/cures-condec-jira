package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.calculation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.ChangeImpactAnalysisConfiguration;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.ChangeImpactAnalysisService;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.ChangePropagationRule;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.KnowledgeElementWithImpact;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Link;
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

	public static List<KnowledgeElementWithImpact> calculateChangeImpact(KnowledgeElement currentElement,
			double parentImpact, FilterSettings filterSettings, List<KnowledgeElementWithImpact> impactedElements,
			long context) {
		ChangeImpactAnalysisConfiguration ciaConfig = filterSettings.getChangeImpactAnalysisConfig();

		// Add link recommmendations to root element if setting has been selected
		Set<Link> currentElementLinks = new HashSet<>(currentElement.getLinks());
		if (filterSettings.getSelectedElement() == currentElement && filterSettings.areLinksRecommended()
				&& filterSettings.getChangeImpactAnalysisConfig().getAreLinkRecommendationsIncludedInCalculation()) {
			ContextInformation linkRecommender = new ContextInformation(filterSettings.getSelectedElement(),
					filterSettings.getLinkRecommendationConfig());
			List<LinkRecommendation> linkRecommendations = linkRecommender.getLinkRecommendations();
			for (LinkRecommendation recommendation : linkRecommendations) {
				if (!recommendation.isDiscarded()) {
					currentElementLinks.add(recommendation);
				}
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

			// Calculate distinct impact values
			double linkTypeWeight = ciaConfig.getLinkImpact().getOrDefault(linkTypeName, 1.0f);
			double decayValue = ciaConfig.getDecayValue();
			double ruleBasedValue = calculatePropagationRuleImpact(filterSettings, nextElementInPath, link);
			double impactValue = parentImpact * linkTypeWeight * (1 - decayValue) * ruleBasedValue;
			String impactExplanation = "";

			// Add LinkRecommendationScore to impactExplanation if the element was a
			// recommendation
			if (link.getClass() == LinkRecommendation.class) {
				LinkRecommendation recommendation = (LinkRecommendation) link;
				double linkRecommendationScore = recommendation.getScore().getValue() / 100;
				impactValue = impactValue * linkRecommendationScore;
				impactExplanation = Tooltip.generateImpactExplanation(parentImpact, ruleBasedValue, decayValue, impactValue,
						linkTypeName, linkRecommendationScore);
			} else {
				impactExplanation = Tooltip.generateImpactExplanation(parentImpact, ruleBasedValue, decayValue, impactValue,
						linkTypeName, 0);
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
	 * @issue How should we handle the rule weight when calculating the rule impact
	 *        value?
	 * @alternative The rule weight is used to adjust the individual rule result
	 *              based on the outcome, e.g. with a high rule weight: relatively
	 *              strong propagation values are further increased while weak
	 *              values are decreased. Thus, scoring "hits" during rule
	 *              calculation has more impact and vice versa.
	 * @con There would be no way to increase the importance of rules in comparison
	 *      to others. All rules would be equal in the end, regardless of individual
	 *      rule results.
	 * @decision The rule weight is used to specify the importance of a rule in
	 *           comparison to all other rules.
	 * @pro Allows more control over the overall rule based calculation.
	 *
	 * @return Double containing the calculated propagation rule score, value
	 *         between 0 and 1.0
	 */
	public static double calculatePropagationRuleImpact(FilterSettings filterSettings, KnowledgeElement currentElement,
			Link link) {
		Map<String, Double> mapOfRules = new HashMap<>();
		double ruleValueResult = 0.0;
		double maxAchievableScore = 0.0;

		// Each rule is individually mapped with its description and corresponding
		// impact score
		for (ChangePropagationRule rule : filterSettings.getChangeImpactAnalysisConfig().getPropagationRules()) {
			if (!rule.isActive()) {
				continue;
			}
			double ruleWeightValue = ChangePropagationRule.getWeightForRule(filterSettings, rule.getType());
			maxAchievableScore += Math.abs(ruleWeightValue);
			double ruleCalculationValue = rule.getType().getFunction().isChangePropagated(filterSettings,
					currentElement, link);
			// Reverse rule effect if weight is negative
			if (ruleWeightValue < 0) {
				ruleCalculationValue = 1.0 - ruleCalculationValue;
			}
			// Apply weight onto rule impact
			ruleCalculationValue *= Math.abs(ruleWeightValue);
			ruleValueResult += ruleCalculationValue;
			mapOfRules.put(
				ruleWeightValue < 0
					? "Do not " + rule.getType().getDescription().toLowerCase() 
					: rule.getType().getDescription()
				, ruleCalculationValue);
		}
		propagationRuleResult = mapOfRules;
		return ruleValueResult / maxAchievableScore;
	}
}
