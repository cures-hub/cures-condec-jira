package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.Link;

/**
 * Rule that defines that a change impact is stronger propagated if the
 * traversed {@link KnowledgeElement} in the {@link KnowledgeGraph} was
 * quickly finished.
 */
public class BoostWhenQuicklyFinished implements ChangePropagationFunction {

	@Override
	public double isChangePropagated(FilterSettings filterSettings, KnowledgeElement nextElement, Link link) {
		double differenceInWeeks = (nextElement.getCreationDate().getTime()
				- nextElement.getLatestUpdatingDate().getTime()) / (1000 * 60 * 60 * 24) / 7;
		double propagationRuleResult = 1 + (differenceInWeeks * 0.1);
		return propagationRuleResult < 0 ? 0.0 : propagationRuleResult;
	}
}