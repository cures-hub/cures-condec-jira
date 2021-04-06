package de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluation.metrics;

import java.util.List;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.Recommendation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;

/**
 * Measures the recall (true positive rate/sensitivity) within the top-k
 * results, i.e. the fraction of the solution options in the ground truth that
 * are successfully recommended.
 * 
 * Since not all existing solution options might be recommended, the recall is
 * interesting for solution option recommendation.
 */
public class Recall extends EvaluationMetric {

	private double numberOfTruePositives;
	private double numberOfFalseNegatives;

	public Recall(List<Recommendation> recommendations, List<KnowledgeElement> groundTruthSolutionOptions) {
		super(recommendations, groundTruthSolutionOptions);
		this.numberOfTruePositives = new NumberOfTruePositives(recommendations, groundTruthSolutionOptions)
				.calculateMetric();
		this.numberOfFalseNegatives = groundTruthSolutionOptions.size() - numberOfTruePositives;
	}

	public Recall(double numberOfTruePositives, double numberOfFalseNegatives) {
		super(null);
		this.numberOfTruePositives = numberOfTruePositives;
		this.numberOfFalseNegatives = numberOfFalseNegatives;
	}

	@Override
	public double calculateMetric() {
		double recall = numberOfTruePositives / (numberOfTruePositives + numberOfFalseNegatives);
		return !Double.isNaN(recall) ? recall : 0.0;
	}

	@Override
	public String getName() {
		return "Recall(@k)";
	}

	@Override
	public String getDescription() {
		return "Measures the recall (true positive rate/sensitivity) within the top-k results, i.e. "
				+ "the fraction of the solution options in the ground truth that are successfully recommended.";
	}
}
