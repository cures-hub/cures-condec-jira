package de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.evaluation.metrics;

import java.util.List;

import de.uhd.ifi.se.decision.management.jira.model.SolutionOption;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.ElementRecommendation;

/**
 * Measures the precision (positive predictive value) within the top-k results,
 * i.e. the fraction of relevant recommendations (that match the solution
 * options in the ground truth) among the retrieved recommendations.
 */
public class Precision extends EvaluationMetric {

	private double numberOfTruePositives;

	public Precision(List<ElementRecommendation> recommendations, List<SolutionOption> solutionOptions) {
		super(recommendations, solutionOptions);
		this.numberOfTruePositives = new NumberOfTruePositives(recommendations, groundTruthSolutionOptions)
				.calculateMetric();
	}

	public Precision(List<ElementRecommendation> recommendations, double numberOfTruePositives) {
		super(recommendations);
		this.numberOfTruePositives = numberOfTruePositives;
	}

	@Override
	public double calculateMetric() {
		double precision = numberOfTruePositives / (recommendations.size());
		return !Double.isNaN(precision) ? precision : 0.0;
	}

	@Override
	public String getName() {
		return "Precision(@k)";
	}

	@Override
	public String getDescription() {
		return "Measures the precision (positive predictive value) within the top-k results, i.e. "
				+ "the fraction of relevant recommendations (that match the solution "
				+ "options in the ground truth) among the retrieved recommendations.";
	}
}
