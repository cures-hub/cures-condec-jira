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

	/**
	 * @see <a href="https://en.wikipedia.org/w/index.php?title=Confusion_matrix&oldid=1058352752">
	 *     Wikipedia page about confusion matrices</a>
	 */
	private final double numberOfTruePositives;

	/**
	 * @param recommendations {@link EvaluationMetric#recommendations}
	 * @param solutionOptions {@link EvaluationMetric#groundTruthSolutionOptions}
	 */
	public Precision(List<ElementRecommendation> recommendations, List<SolutionOption> solutionOptions) {
		super(recommendations, solutionOptions);
		this.numberOfTruePositives = new NumberOfTruePositives(recommendations, groundTruthSolutionOptions)
				.calculateMetric();
	}

	/**
	 * @param recommendations {@link EvaluationMetric#recommendations}
	 * @param numberOfTruePositives {@link Precision#numberOfTruePositives}
	 */
	public Precision(List<ElementRecommendation> recommendations, double numberOfTruePositives) {
		super(recommendations);
		this.numberOfTruePositives = numberOfTruePositives;
	}

	@Override
	public double calculateMetric() {
		double precision = numberOfTruePositives / (recommendations.size());
		return Double.isNaN(precision) ? 0.0 : precision;
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

	/**
	 * @return {@link Precision#numberOfTruePositives}
	 */
	public double getNumberOfTruePositives() {
		return numberOfTruePositives;
	}
}
