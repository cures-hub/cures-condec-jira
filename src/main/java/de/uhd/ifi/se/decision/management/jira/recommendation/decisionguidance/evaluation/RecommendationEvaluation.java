package de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.evaluation;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import de.uhd.ifi.se.decision.management.jira.model.SolutionOption;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.ElementRecommendation;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.KnowledgeSource;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.evaluation.metrics.EvaluationMetric;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.evaluation.metrics.NumberOfTruePositives;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.evaluation.metrics.ReciprocalRank;

/**
 * Represents the evaluation result for one {@link KnowledgeSource} for given
 * inputs. Calculated by the {@link Evaluator}.
 * 
 * Comprises the ground truth solution options, the
 * {@link ElementRecommendation}s, and various {@link EvaluationMetric}s.
 */
public class RecommendationEvaluation {

	/**
	 * Source of the {@link ElementRecommendation}s to be evaluated
	 */
	private KnowledgeSource knowledgeSource;

	/**
	 * Recommendations to be evaluated
	 */
	private List<ElementRecommendation> recommendations;

	/**
	 * Metrics to be used in the evaluation
	 */
	private List<EvaluationMetric> metrics;

	/**
	 * Ground truth of fitting recommendations to which the new ones are compared
	 */
	private List<SolutionOption> groundTruthSolutionOptions;

	/**
	 * @param knowledgeSource {@link RecommendationEvaluation#knowledgeSource}
	 * @param recommendations {@link RecommendationEvaluation#recommendations}
	 * @param metrics {@link RecommendationEvaluation#metrics}
	 * @param solutionOptions {@link RecommendationEvaluation#groundTruthSolutionOptions}
	 */
	public RecommendationEvaluation(KnowledgeSource knowledgeSource, List<ElementRecommendation> recommendations,
			List<EvaluationMetric> metrics, List<SolutionOption> solutionOptions) {
		this.knowledgeSource = knowledgeSource;
		this.recommendations = recommendations;
		this.metrics = metrics;
		this.groundTruthSolutionOptions = solutionOptions;
	}

	/**
	 * @return evaluated {@link KnowledgeSource}.
	 */
	@XmlElement
	public KnowledgeSource getKnowledgeSource() {
		return knowledgeSource;
	}

	/**
	 * @param knowledgeSource
	 *            {@link KnowledgeSource} that is evaluated.
	 */
	public void setKnowledgeSource(KnowledgeSource knowledgeSource) {
		this.knowledgeSource = knowledgeSource;
	}

	/**
	 * @return number of {@link ElementRecommendation}s generated from the
	 *         {@link KnowledgeSource}.
	 */
	@XmlElement
	public List<ElementRecommendation> getRecommendations() {
		return recommendations;
	}

	/**
	 * @param recommendations {@link RecommendationEvaluation#recommendations}
	 */
	public void setRecommendations(List<ElementRecommendation> recommendations) {
		this.recommendations = recommendations;
	}

	/**
	 * @return all metrics calculated using the ground truth solution options and
	 *         the recommendations, such as {@link NumberOfTruePositives} and
	 *         {@link ReciprocalRank}.
	 */
	@XmlElement
	public List<EvaluationMetric> getMetrics() {
		return metrics;
	}

	/**
	 * @param metrics  all metrics to be calculated using the ground truth solution options and
	 *         the recommendations, e.g. {@link NumberOfTruePositives} and
	 *         {@link ReciprocalRank}.
	 */
	public void setMetrics(List<EvaluationMetric> metrics) {
		this.metrics = metrics;
	}

	/**
	 * @return gold standard/ground truth that was already documented.
	 */
	@XmlElement
	public List<SolutionOption> getGroundTruthSolutionOptions() {
		return groundTruthSolutionOptions;
	}

	/**
	 * @param groundTruthSolutionOptions  Gold standard/ground truth that was already documented to be used by
	 *                                    {@link EvaluationMetric}s.
	 */
	public void setGroundTruthSolutionOptions(List<SolutionOption> groundTruthSolutionOptions) {
		this.groundTruthSolutionOptions = groundTruthSolutionOptions;
	}
}
