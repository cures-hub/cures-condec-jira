package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules;

import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.ChangePropagationRule;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.ChangePropagationRuleType;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.Link;

/**
 * Rule that defines that a change impact is stronger propagated if the
 * traversed {@link KnowledgeElement} in the {@link KnowledgeGraph} has a low
 * average age.
 */
public class BoostWhenLowAverageAge implements ChangePropagationFunction {

	@Override
	public double isChangePropagated(FilterSettings filterSettings, KnowledgeElement nextElement, Link link) {
		float ruleWeight = ChangePropagationRule.getWeightForRule(filterSettings,
				ChangePropagationRuleType.BOOST_WHEN_LOW_AVERAGE_AGE);

		double differenceInWeeks = Math.abs((nextElement.getCreationDate().getTime()
				- nextElement.getLatestUpdatingDate().getTime()) / (1000 * 60 * 60 * 24) / 7);
		double result;
		// Reverse effects of rule result for negative weights
		if (ruleWeight < 0) {
			result = (differenceInWeeks / 100) + 0.75;
		} else {
			result = Math.pow(2, ((-1 * differenceInWeeks) / 50));
		}
		if (result <= 0.75) {
			return 0.75;
		} 
		if (result >= 1.0) {
			return 1.0;
		}
		return result;
	}
}
