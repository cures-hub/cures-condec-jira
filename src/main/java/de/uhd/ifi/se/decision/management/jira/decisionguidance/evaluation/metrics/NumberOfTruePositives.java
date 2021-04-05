package de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluation.metrics;

import java.util.List;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.Recommendation;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.KnowledgeSource;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;

/**
 * Counts the number of true positives under the top-k results, i.e. the number
 * of {@link Recommendation}s from a {@link KnowledgeSource} that were already
 * documented in the {@link KnowledgeGraph}.
 */
public class NumberOfTruePositives extends EvaluationMetric {

	public NumberOfTruePositives(List<Recommendation> recommendations, List<KnowledgeElement> solutionOptions,
			int topKResults) {
		super(recommendations, solutionOptions, topKResults);
	}

	public NumberOfTruePositives(List<Recommendation> recommendations,
			List<KnowledgeElement> documentedSolutionOptions) {
		super(recommendations, documentedSolutionOptions);
	}

	@Override
	public String getName() {
		return "#True Positives";
	}

	@Override
	public String getDescription() {
		return "Provides the number of correctly recommended results within the top-k results.";
	}

	@Override
	public double calculateMetric() {
		int numberOfTruePositives = 0;
		for (Recommendation recommendation : recommendations) {
			if (countIntersections(documentedSolutionOptions, recommendation.getSummary()) > 0) {
				numberOfTruePositives++;
			}
		}
		return numberOfTruePositives;
	}
}
