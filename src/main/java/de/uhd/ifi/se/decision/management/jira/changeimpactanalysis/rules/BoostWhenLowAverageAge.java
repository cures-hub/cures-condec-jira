package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules;

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
		// TODO
		float ruleWeight = 1;

		double differenceInDays = (nextElement.getCreationDate().getTime()
				- nextElement.getLatestUpdatingDate().getTime()) / (1000 * 60 * 60 * 24);
		return Math.pow(2, (differenceInDays / 100)) * (2 - ruleWeight) < 0.75 ? 0.75
				: Math.pow(2, (differenceInDays / 100)) * (2 - ruleWeight);
	}
}
