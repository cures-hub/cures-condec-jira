package de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.evaluation.metrics;

import java.util.List;

import de.uhd.ifi.se.decision.management.jira.model.SolutionOption;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.Recommendation;

/**
 * Measures the harmonic mean of {@link Precision} and {@Recall}.
 */
public class FScore extends EvaluationMetric {

	private double precision;
	private double recall;

	public FScore(List<Recommendation> recommendations, List<SolutionOption> solutionOptions) {
		super(recommendations, solutionOptions);
		this.precision = new Precision(recommendations, solutionOptions).calculateMetric();
		this.recall = new Recall(recommendations, solutionOptions).calculateMetric();
	}

	public FScore(double precision, double recall) {
		super(null);
		this.precision = precision;
		this.recall = recall;
	}

	@Override
	public double calculateMetric() {
		double fScore = 2 * precision * recall / (precision + recall);
		return !Double.isNaN(fScore) ? fScore : 0.0;
	}

	@Override
	public String getName() {
		return "F-Score";
	}

	@Override
	public String getDescription() {
		return "Measures the harmonic mean of precision and recall. Uses the top-k results!";
	}
}
