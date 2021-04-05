package de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluation.metrics;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.Recommendation;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.KnowledgeSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.score.RecommendationScore;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;

/**
 * Abstract superclass for evaluation metrics, such as
 * {@link NumberOfTruePositives} and {@link ReciprocalRank}.
 */
public abstract class EvaluationMetric {

	/**
	 * Gold standard/ground truth that is already documented.
	 */
	protected List<KnowledgeElement> documentedSolutionOptions;
	protected List<Recommendation> recommendations;

	/**
	 * @param recommendations
	 *            {@link Recommendation}s from a {@link KnowledgeSource}, such as
	 *            DBPedia or a Jira project.
	 * @param solutionOptions
	 *            gold standard/ground truth that was already documented.
	 * @param topKResults
	 *            number of {@link Recommendation}s with the highest
	 *            {@link RecommendationScore} included in the evaluation. All other
	 *            recommendations are ignored.
	 */
	public EvaluationMetric(List<Recommendation> recommendations, List<KnowledgeElement> solutionOptions,
			int topKResults) {
		this.recommendations = getTopKRecommendations(recommendations, topKResults);
		this.documentedSolutionOptions = solutionOptions;
	}

	@XmlElement(name = "value")
	public abstract double calculateMetric();

	@XmlElement
	public abstract String getName();

	@XmlElement
	public abstract String getDescription();

	/**
	 * 
	 * @param allRecommendations
	 *            all {@link Recommendation}s generated from the
	 *            {@link KnowledgeSource} sorted by their
	 *            {@link RecommendationScore}.
	 * @param k
	 *            for the top-k recommendations.
	 * @return the top-k {@link Recommendation}s with the hightest
	 *         {@link RecommendationScore}s.
	 */
	private static List<Recommendation> getTopKRecommendations(List<Recommendation> allRecommendations, int k) {
		if (k < allRecommendations.size()) {
			return allRecommendations.subList(0, k);
		}
		return allRecommendations;
	}
}