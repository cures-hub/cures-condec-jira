package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules;

import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.ChangePropagationRule;
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
		float ruleWeight = filterSettings.getChangeImpactAnalysisConfig().getPropagationRules()
			.get(filterSettings.getChangeImpactAnalysisConfig().getPropagationRules()
				.indexOf(ChangePropagationRule.BOOST_WHEN_TEXTUAL_SIMILAR))
			.getWeightValue();

		float similarityScore = similarityProvider.assessRelation(filterSettings.getSelectedElement(), nextElement)
				.getValue();
		return similarityScore * (2 - ruleWeight) >= 1.0
			? 1.0
			: similarityScore * (2 - ruleWeight);
	}
}
