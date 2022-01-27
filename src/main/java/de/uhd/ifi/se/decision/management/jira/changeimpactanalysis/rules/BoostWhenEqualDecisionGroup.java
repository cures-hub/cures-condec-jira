package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules;

import java.util.Set;
import java.util.stream.Collectors;

import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.ChangePropagationRule;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.ChangePropagationRuleType;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.Link;

/**
 * Rule that defines that a change impact is stronger propagated if the
 * traversed {@link KnowledgeElement} in the {@link KnowledgeGraph} is of equal
 * decision group.
 */
public class BoostWhenEqualDecisionGroup implements ChangePropagationFunction {

	@Override
	public double isChangePropagated(FilterSettings filterSettings, KnowledgeElement nextElement, Link link) {
		float ruleWeight = ChangePropagationRule.getWeightForRule(filterSettings,
				ChangePropagationRuleType.BOOST_WHEN_EQUAL_DECISION_GROUP);
		double weightFactor = (0.75 * (2 - ruleWeight)) >= 1.0 ? 1.0 : (0.75 * (2 - ruleWeight));

		if (!filterSettings.getSelectedElement().getDecisionGroups().isEmpty()) {
			if (nextElement.getDecisionGroups().isEmpty()) {
				return weightFactor;
			}
			Set<String> setOfMatchingDecisionGroups = filterSettings.getSelectedElement().getDecisionGroups().stream()
					.filter(item -> nextElement.getDecisionGroups().contains(item)).collect(Collectors.toSet());
			return setOfMatchingDecisionGroups.isEmpty() ? weightFactor : 1.0;
		}
		return 1.0;
	}
}
