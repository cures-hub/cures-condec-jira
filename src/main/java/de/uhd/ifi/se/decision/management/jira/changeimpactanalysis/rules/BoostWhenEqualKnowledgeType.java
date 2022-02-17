package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation.KnowledgeTypeContextInformationProvider;

/**
 * Rule that defines that a change impact is stronger propagated if the
 * traversed {@link KnowledgeElement} in the {@link KnowledgeGraph}
 * has the same  {@link KnowledgeType}.
 */
public class BoostWhenEqualKnowledgeType implements ChangePropagationFunction {

    private static final KnowledgeTypeContextInformationProvider similarityProvider = new KnowledgeTypeContextInformationProvider();

    @Override
    public double isChangePropagated(FilterSettings filterSettings, KnowledgeElement nextElement, Link link) {
		return similarityProvider.assessRelation(filterSettings.getSelectedElement(), nextElement)
				.getValue();
	}
}
