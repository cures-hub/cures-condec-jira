package de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluationframework.evaluationmethods;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;

import java.util.List;

public class AveragePrecision implements EvaluationMethod {

	@Override
	public double calculateMetric(List<Recommendation> recommendations, List<KnowledgeElement> solutionOptions, int topKResults) {
		double precisionK = 0.0;
		double relevantItems = 0.0;

		int k = recommendations.size() <= topKResults ? recommendations.size() : topKResults;
		for (int i = 0; i < k; i++) {
			for (KnowledgeElement solutionOption : solutionOptions) {
				if (solutionOption.getSummary().trim().contains(recommendations.get(i).getRecommendations().trim()) ||
					recommendations.get(i).getRecommendations().trim().contains(solutionOption.getSummary().trim())) {
					relevantItems += 1.0;
					precisionK += (relevantItems / (i + 1.0));
				}
			}
		}
		double AP = precisionK / relevantItems;
		return !Double.isNaN(AP) ? AP : 0.0;
	}

	@Override
	public String toString() {
		return "Average Precision";
	}
}
