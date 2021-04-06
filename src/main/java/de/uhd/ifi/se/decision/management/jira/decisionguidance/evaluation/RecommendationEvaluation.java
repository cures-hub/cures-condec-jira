package de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluation;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.Recommendation;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluation.metrics.EvaluationMetric;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.KnowledgeSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.recommender.RecommenderType;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;

/**
 * Represents the evaluation result for one knowledge source with given inputs.
 * Calculated by the {@link EvaluationRecommender}.
 * 
 * Comprises various metrics.
 */
public class RecommendationEvaluation {

	private RecommenderType recommenderType;
	private KnowledgeSource knowledgeSource;
	private List<Recommendation> recommendations;
	private List<EvaluationMetric> metrics;
	private List<KnowledgeElement> groundTruthSolutionOptions;

	public RecommendationEvaluation(RecommenderType recommenderType, KnowledgeSource knowledgeSource,
			List<Recommendation> recommendations, List<EvaluationMetric> metrics,
			List<KnowledgeElement> solutionOptions) {
		this.recommenderType = recommenderType;
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
	 * @return number of {@link Recommendation}s generated from the
	 *         {@link KnowledgeSource}.
	 */
	@XmlElement
	public List<Recommendation> getRecommendations() {
		return recommendations;
	}

	/**
	 * @param numberOfResults
	 *            number of {@link Recommendation}s generated from the
	 *            {@link KnowledgeSource}.
	 */
	public void setRecommendations(List<Recommendation> recommendations) {
		this.recommendations = recommendations;
	}

	@XmlElement
	public RecommenderType getRecommenderType() {
		return recommenderType;
	}

	public void setRecommenderType(RecommenderType recommenderType) {
		this.recommenderType = recommenderType;
	}

	@XmlElement
	public List<EvaluationMetric> getMetrics() {
		return metrics;
	}

	/**
	 * @return gold standard/ground truth that was already documented.
	 */
	@XmlElement
	public List<KnowledgeElement> getGroundTruthSolutionOptions() {
		return groundTruthSolutionOptions;
	}
}
