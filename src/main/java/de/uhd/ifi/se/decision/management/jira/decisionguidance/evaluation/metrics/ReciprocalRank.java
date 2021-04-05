package de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluation.metrics;

import java.util.List;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.Recommendation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;

public class ReciprocalRank extends EvaluationMetric {

	public ReciprocalRank(List<Recommendation> recommendations, List<KnowledgeElement> solutionOptions,
			int topKResults) {
		super(recommendations, solutionOptions, topKResults);
	}

	@Override
	public double calculateMetric() {
		double sum_RR = 0.0;
		for (int i = 0; i < recommendations.size(); i++) {
			for (KnowledgeElement solutionOption : documentedSolutionOptions) {
				if (solutionOption.getSummary().trim().contains(recommendations.get(i).getSummary().trim())
						|| recommendations.get(i).getSummary().trim().contains(solutionOption.getSummary().trim())) {
					sum_RR += (1.0 / (i + 1));
					return !Double.isNaN(sum_RR) ? sum_RR : 0.0;
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
		return "Measures the position of the first correct result";
	}
}
