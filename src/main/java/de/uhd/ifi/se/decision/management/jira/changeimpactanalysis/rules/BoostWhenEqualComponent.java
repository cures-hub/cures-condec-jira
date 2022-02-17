package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation.ComponentContextInformationProvider;

/**
 * Rule that defines that a change impact is stronger propagated if the
 * traversed {@link KnowledgeElement} in the {@link KnowledgeGraph} is 
 * assigned to the same component.
 */
public class BoostWhenEqualComponent implements ChangePropagationFunction {

	private static final ComponentContextInformationProvider similarityProvider = new ComponentContextInformationProvider();

	@Override
	public double isChangePropagated(FilterSettings filterSettings, KnowledgeElement nextElement, Link link) {
		return similarityProvider.assessRelation(filterSettings.getSelectedElement(), nextElement)
				.getValue();
	}
}
