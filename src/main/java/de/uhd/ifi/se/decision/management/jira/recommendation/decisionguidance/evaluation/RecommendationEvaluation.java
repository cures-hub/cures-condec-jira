package de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.evaluation;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import de.uhd.ifi.se.decision.management.jira.model.SolutionOption;
import de.uhd.ifi.se.decision.management.jira.recommendation.Recommendation;
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

	private KnowledgeSource knowledgeSource;
	private List<ElementRecommendation> recommendations;
	private List<EvaluationMetric> metrics;
	private List<SolutionOption> groundTruthSolutionOptions;

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
	 * @param numberOfResults
	 *            number of {@link ElementRecommendation}s generated from the
	 *            {@link KnowledgeSource}.
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
	 * @return gold standard/ground truth that was already documented.
	 */
	@XmlElement
	public List<SolutionOption> getGroundTruthSolutionOptions() {
		return groundTruthSolutionOptions;
	}
}
