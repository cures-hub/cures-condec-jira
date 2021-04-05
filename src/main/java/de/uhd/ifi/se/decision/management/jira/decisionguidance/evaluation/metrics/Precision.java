package de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluation.metrics;

import java.util.List;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.Recommendation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;

public class Precision extends EvaluationMetric {

	public Precision(List<Recommendation> recommendations, List<KnowledgeElement> solutionOptions, int topKResults) {
		super(recommendations, solutionOptions, topKResults);
	}

	@Override
	public double calculateMetric() {
		double numberOfTruePositives = new NumberOfTruePositives(recommendations, documentedSolutionOptions)
				.calculateMetric();
		double precision = numberOfTruePositives / recommendations.size();
		return !Double.isNaN(precision) ? precision : 0.0;
	}

	@Override
	public String getName() {
		return "Precision(@k)";
	}

	@Override
	public String getDescription() {
		return "Measures the precision within the top-k results, i.e. "
				+ "the fraction of relevant recommendations among the retrieved recommendations.";
	}
}
