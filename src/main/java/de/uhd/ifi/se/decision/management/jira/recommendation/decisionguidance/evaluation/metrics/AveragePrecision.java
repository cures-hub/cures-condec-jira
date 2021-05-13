package de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.evaluation.metrics;

import java.util.List;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.SolutionOption;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.Recommendation;

/**
 * Measures the average precision (AP) within the top-k results. Takes the total
 * number of ground truth positives into account, i.e. the number of the
 * solution options already documented.
 * 
 * https://towardsdatascience.com/breaking-down-mean-average-precision-map-ae462f623a52
 */
public class AveragePrecision extends EvaluationMetric {

	public AveragePrecision(List<Recommendation> recommendations, List<SolutionOption> solutionOptions) {
		super(recommendations, solutionOptions);
	}

	@Override
	public double calculateMetric() {
		int numberOfMatches = 0;
		double precisionAtK = 0.0;
		// GTP refers to the total number of ground truth positives
		double numberOfGroundTruthPositives = groundTruthSolutionOptions.size();

		for (int i = 0; i < recommendations.size(); i++) {
			for (KnowledgeElement solutionOption : groundTruthSolutionOptions) {
				if (isMatching(solutionOption, recommendations.get(i))) {
					precisionAtK += (++numberOfMatches / (i + 1.0));
				}
			}
		}
		double averagePrecision = precisionAtK / numberOfGroundTruthPositives;
		return !Double.isNaN(averagePrecision) ? averagePrecision : 0.0;
	}

	@Override
	public String getName() {
		return "Average Precision";
	}

	@Override
	public String getDescription() {
		return "Measures the average precision within the top-k results. Takes the total"
				+ "number of ground truth positives into account, i.e. the number of the "
				+ "solution options already documented.";
	}
}
