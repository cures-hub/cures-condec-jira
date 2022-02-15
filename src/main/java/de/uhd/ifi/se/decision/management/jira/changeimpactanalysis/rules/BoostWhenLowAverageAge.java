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
		double differenceInWeeks = (nextElement.getCreationDate().getTime()
				- nextElement.getLatestUpdatingDate().getTime()) / (1000 * 60 * 60 * 24) / 7;
		return Math.pow(2, (differenceInWeeks / 8));
	}
}