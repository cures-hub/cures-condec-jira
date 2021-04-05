package de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluation.evaluationmethods;

import java.util.List;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.Recommendation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;

public class AveragePrecision extends EvaluationMethod {

	public AveragePrecision(List<Recommendation> recommendations, List<KnowledgeElement> solutionOptions,
			int topKResults) {
		this.recommendations = recommendations;
		this.solutionOptions = solutionOptions;
		this.topKResults = topKResults;
	}

	@Override
	public double calculateMetric() {
		double precisionK = 0.0;
		double relevantItems = 0.0;

		int k = recommendations.size() <= topKResults ? recommendations.size() : topKResults;
		for (int i = 0; i < k; i++) {
			for (KnowledgeElement solutionOption : solutionOptions) {
				if (solutionOption.getSummary().trim().contains(recommendations.get(i).getSummary().trim())
						|| recommendations.get(i).getSummary().trim().contains(solutionOption.getSummary().trim())) {
					relevantItems += 1.0;
					precisionK += relevantItems / (i + 1.0);
				}
			}
		}
		double AP = precisionK / relevantItems;
		return !Double.isNaN(AP) ? AP : 0.0;
	}

	@Override
	public String getName() {
		return "Average Precision";
	}

	@Override
	public String getDescription() {
		return "Measures the average precision with the top k results.";
	}
}
