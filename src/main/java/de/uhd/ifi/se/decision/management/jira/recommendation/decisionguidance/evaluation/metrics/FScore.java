package de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.evaluation.metrics;

import java.util.List;

import de.uhd.ifi.se.decision.management.jira.model.SolutionOption;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.ElementRecommendation;

/**
 * Measures the harmonic mean of {@link Precision} and {@link Recall}.
 */
public class FScore extends EvaluationMetric {

	/**
	 * @see <a href="https://en.wikipedia.org/w/index.php?title=Precision_and_recall&oldid=1069330375#Precision">
	 *     Wikipedia entry for Precision</a>
	 */
	private final double precision;

	/**
	 * @see <a href="https://en.wikipedia.org/w/index.php?title=Precision_and_recall&oldid=1069330375#Recall">
	 * 	    Wikipedia entry for Recall</a>
	 */
	private final double recall;

	/**
	 * @param recommendations Recommendations to be evaluated.
	 * @param solutionOptions Ground truth to which the given recommendations are compared.
	 */
	public FScore(List<ElementRecommendation> recommendations, List<SolutionOption> solutionOptions) {
		super(recommendations, solutionOptions);
		this.precision = new Precision(recommendations, solutionOptions).calculateMetric();
		this.recall = new Recall(recommendations, solutionOptions).calculateMetric();
	}

	/**
	 * @param precision {@link FScore#precision}
	 * @param recall {@link FScore#recall}
	 */
	public FScore(double precision, double recall) {
		super(null);
		this.precision = precision;
		this.recall = recall;
	}

	@Override
	public double calculateMetric() {
		double fScore = 2 * precision * recall / (precision + recall);
		return Double.isNaN(fScore) ? 0.0 : fScore;
	}

	@Override
	public String getName() {
		return "F-Score";
	}

	@Override
	public String getDescription() {
		return "Measures the harmonic mean of precision and recall. Uses the top-k results!";
	}

	/**
	 * @return {@link FScore#precision}
	 */
	public double getPrecision() {
		return precision;
	}

	/**
	 * @return {@link FScore#recall}
	 */
	public double getRecall() {
		return recall;
	}
}
