package de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluationframework.evaluationmethods;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;

import java.util.List;

public class ReciprocalRank extends EvaluationMethod {

	public ReciprocalRank(List<Recommendation> recommendations, List<KnowledgeElement> solutionOptions, int topKResults) {
		this.recommendations = recommendations;
		this.solutionOptions = solutionOptions;
		this.topKResults = topKResults;
	}

	@Override
	public double calculateMetric() {
		double sum_RR = 0.0;

		int maxResults = recommendations.size() <= topKResults ? recommendations.size() : topKResults;
		for (int i = 0; i < maxResults; i++) {
			for (KnowledgeElement solutionOption : solutionOptions) {
				if (solutionOption.getSummary().trim().contains(recommendations.get(i).getSummary().trim()) ||
					recommendations.get(i).getSummary().trim().contains(solutionOption.getSummary().trim())) {
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
