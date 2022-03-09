package de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.evaluation.metrics;

import java.util.List;

import de.uhd.ifi.se.decision.management.jira.model.SolutionOption;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.ElementRecommendation;

/**
 * Measures the recall (true positive rate/sensitivity) within the top-k
 * results, i.e. the fraction of the solution options in the ground truth that
 * are successfully recommended.
 * 
 * Since not all existing solution options might be recommended, the recall is
 * interesting for solution option recommendation.
 */
public class Recall extends EvaluationMetric {

	/**
	 * @see <a href="https://en.wikipedia.org/w/index.php?title=Confusion_matrix&oldid=1058352752">
	 * 	    Wikipedia page about confusion matrices</a>
	 */
	private final double numberOfTruePositives;
	/**
	 * @see <a href="https://en.wikipedia.org/w/index.php?title=Confusion_matrix&oldid=1058352752">
	 * 	    Wikipedia page about confusion matrices</a>
	 */
	private final double numberOfFalseNegatives;

	/**
	 * @param recommendations {@link EvaluationMetric#recommendations}
	 * @param groundTruthSolutionOptions {@link EvaluationMetric#groundTruthSolutionOptions}
	 */
	public Recall(List<ElementRecommendation> recommendations, List<SolutionOption> groundTruthSolutionOptions) {
		super(recommendations, groundTruthSolutionOptions);
		this.numberOfTruePositives = new NumberOfTruePositives(recommendations, groundTruthSolutionOptions)
				.calculateMetric();
		this.numberOfFalseNegatives = groundTruthSolutionOptions.size() - numberOfTruePositives;
	}

	/**
	 * @param numberOfTruePositives {@link Recall#numberOfTruePositives}
	 * @param numberOfFalseNegatives {@link Recall#numberOfFalseNegatives}
	 */
	public Recall(double numberOfTruePositives, double numberOfFalseNegatives) {
		super(null);
		this.numberOfTruePositives = numberOfTruePositives;
		this.numberOfFalseNegatives = numberOfFalseNegatives;
	}

	@Override
	public double calculateMetric() {
		double recall = numberOfTruePositives / (numberOfTruePositives + numberOfFalseNegatives);
		if (Double.isNaN(recall)) {
			recall = 0.0;
		}
		recall = Math.min(1.0, recall); // number of true positives is higher than the ground truth size,
		//                                 when various recommendations matched to the same solution option
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

	/**
	 * @return {@link Recall#numberOfTruePositives}
	 */
	public double getNumberOfTruePositives() {
		return numberOfTruePositives;
	}

	/**
	 * @return {@link Recall#numberOfFalseNegatives}
	 */
	public double getNumberOfFalseNegatives() {
		return numberOfFalseNegatives;
	}
}
