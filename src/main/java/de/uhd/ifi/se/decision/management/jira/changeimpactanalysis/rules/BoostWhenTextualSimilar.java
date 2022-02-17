package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation.TextualSimilarityContextInformationProvider;

/**
 * Rule that defines that a change impact is stronger propagated if the
 * traversed {@link KnowledgeElement} in the {@link KnowledgeGraph} is textual
 * similar to the selected element.
 * 
 * @see TextualSimilarityContextInformationProvider
 */
public class BoostWhenTextualSimilar implements ChangePropagationFunction {

	private static final TextualSimilarityContextInformationProvider similarityProvider = new TextualSimilarityContextInformationProvider();

	@Override
	public double isChangePropagated(FilterSettings filterSettings, KnowledgeElement nextElement, Link link) {
		return similarityProvider.assessRelation(filterSettings.getSelectedElement(), nextElement)
				.getValue();
	}
}