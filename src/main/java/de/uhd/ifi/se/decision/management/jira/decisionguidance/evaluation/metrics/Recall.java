package de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluation.metrics;

import java.util.List;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.Recommendation;
import de.uhd.ifi.se.decision.management.jira.model.SolutionOption;

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

	public Recall(List<Recommendation> recommendations, List<SolutionOption> groundTruthSolutionOptions) {
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
		if (Double.isNaN(recall)) {
			return 0.0;
		}
		if (recall > 1.0) {
			// number of true positives is higher than the ground truth size,
			// because various recommendations matched to the same solution option
			return 1.0;
		}
		return recall;
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
