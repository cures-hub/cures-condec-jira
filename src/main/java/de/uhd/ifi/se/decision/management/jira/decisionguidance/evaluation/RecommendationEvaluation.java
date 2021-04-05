package de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluation;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.Recommendation;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluation.metrics.EvaluationMetric;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.KnowledgeSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.recommender.RecommenderType;

/**
 * Represents the evaluation result for one knowledge source with given inputs.
 * Calculated by the {@link EvaluationRecommender}.
 * 
 * Comprises various metrics.
 */
public class RecommendationEvaluation {

	private RecommenderType recommenderType;
	private KnowledgeSource knowledgeSource;
	private int numberOfResults;
	private List<EvaluationMetric> metrics;

	public RecommendationEvaluation(RecommenderType recommenderType, KnowledgeSource knowledgeSource,
			int numberOfResults, List<EvaluationMetric> metrics) {
		this.recommenderType = recommenderType;
		this.knowledgeSource = knowledgeSource;
		this.numberOfResults = numberOfResults;
		this.metrics = metrics;
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
	public int getNumberOfResults() {
		return numberOfResults;
	}

	/**
	 * @param numberOfResults
	 *            number of {@link Recommendation}s generated from the
	 *            {@link KnowledgeSource}.
	 */
	public void setNumberOfResults(int numberOfResults) {
		this.numberOfResults = numberOfResults;
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
}
