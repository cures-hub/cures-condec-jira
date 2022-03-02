package de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.evaluation.metrics;

import java.util.List;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.SolutionOption;
import de.uhd.ifi.se.decision.management.jira.recommendation.Recommendation;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.ElementRecommendation;

/**
 * Measures the position of the first correct recommendation. For example: If
 * the first recommendation is relevant, the reciprocal rank is 1. If the first
 * recommendation is irrelevant and the second recommendation is relevant, the
 * reciprocal rank is 0.5.
 */
public class ReciprocalRank extends EvaluationMetric {

	public ReciprocalRank(List<ElementRecommendation> recommendations, List<SolutionOption> solutionOptions) {
		super(recommendations, solutionOptions);
	}

	@Override
	public double calculateMetric() {
		for (int i = 0; i < recommendations.size(); i++) {
			for (KnowledgeElement solutionOption : groundTruthSolutionOptions) {
				if (isMatching(solutionOption, recommendations.get(i))) {
					return 1.0 / (i + 1);
				}
			}
		}
		return 0.0;
	}

	@Override
	public String getName() {
		return "Reciprocal Rank";
	}

	@Override
	public String getDescription() {
		return "Measures the position of the first correct recommendation.";
	}
}
