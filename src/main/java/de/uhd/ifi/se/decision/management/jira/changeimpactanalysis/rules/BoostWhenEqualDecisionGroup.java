package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules;

import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.ChangePropagationRule;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.ChangePropagationRuleType;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation.DecisionGroupContextInformationProvider;

/**
 * Rule that defines that a change impact is stronger propagated if the
 * traversed {@link KnowledgeElement} in the {@link KnowledgeGraph} is of equal
 * decision group.
 */
public class BoostWhenEqualDecisionGroup implements ChangePropagationFunction {

	private static final DecisionGroupContextInformationProvider similarityProvider = new DecisionGroupContextInformationProvider();

	@Override
	public double isChangePropagated(FilterSettings filterSettings, KnowledgeElement nextElement, Link link) {
		float ruleWeight = ChangePropagationRule.getWeightForRule(filterSettings,
				ChangePropagationRuleType.BOOST_WHEN_EQUAL_DECISION_GROUP);
		float similarityScore = similarityProvider.assessRelation(filterSettings.getSelectedElement(), nextElement)
				.getValue();
		return ChangePropagationRule.addWeightValue(ruleWeight, similarityScore);
	}
}
