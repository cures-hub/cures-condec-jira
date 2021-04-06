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
 * 
 * Use {@link #getTopKRecommendations(List, int)} to trim the list of
 * {@link Recommendation}s to the top-k results.
 */
public abstract class EvaluationMetric {

	/**
	 * Gold standard/ground truth that is already documented.
	 */
	protected List<KnowledgeElement> groundTruthSolutionOptions;
	protected List<Recommendation> recommendations;

	public EvaluationMetric(List<Recommendation> recommendations) {
		this.recommendations = recommendations;
	}

	/**
	 * @param recommendations
	 *            {@link Recommendation}s from a {@link KnowledgeSource}, such as
	 *            DBPedia or a Jira project.
	 * @param groundTruthSolutionOptions
	 *            gold standard/ground truth that was already documented.
	 */
	public EvaluationMetric(List<Recommendation> recommendations, List<KnowledgeElement> groundTruthSolutionOptions) {
		this(recommendations);
		this.groundTruthSolutionOptions = groundTruthSolutionOptions;
	}

	/**
	 * @return metric value, e.g. for {@link Precision} or {@link ReciprocalRank}.
	 */
	@XmlElement(name = "value")
	public abstract double calculateMetric();

	/**
	 * @return name of the metric that is shown in the user interface (settings
	 *         page).
	 */
	@XmlElement
	public abstract String getName();

	/**
	 * @return description of the metric that is shown in the user interface
	 *         (settings page).
	 */
	@XmlElement
	public abstract String getDescription();

	public List<Recommendation> getRecommendations() {
		return recommendations;
	}

	/**
	 * @param allRecommendations
	 *            all {@link Recommendation}s generated from the
	 *            {@link KnowledgeSource} sorted by their
	 *            {@link RecommendationScore}.
	 * @param k
	 *            number of {@link Recommendation}s with the highest
	 *            {@link RecommendationScore} included in the evaluation. All other
	 *            recommendations are ignored.
	 * @return the top-k {@link Recommendation}s with the hightest
	 *         {@link RecommendationScore}s.
	 */
	public static List<Recommendation> getTopKRecommendations(List<Recommendation> allRecommendations, int k) {
		if (k <= 0 || k >= allRecommendations.size()) {
			return allRecommendations;
		}
		return allRecommendations.subList(0, k);
	}

	/**
	 * @param knowledgeElements
	 *            list of already documented solution options whose summary is
	 *            matched against the recommendation.
	 * @param matchingString
	 *            summary of one single recommendation.
	 * @return number of matches.
	 */
	protected static int countMatches(List<KnowledgeElement> knowledgeElements, String matchingString) {
		int counter = 0;
		for (KnowledgeElement knowledgeElement : knowledgeElements) {
			if (isMatching(knowledgeElement.getSummary(), matchingString)) {
				counter++;
			}
		}
		return counter;
	}

	protected static boolean isMatching(KnowledgeElement knowledgeElement, Recommendation recommendation) {
		return isMatching(knowledgeElement.getSummary(), recommendation.getSummary());
	}

	protected static boolean isMatching(String summaryA, String summaryB) {
		return summaryA.toLowerCase().contains(summaryB.toLowerCase().trim())
				|| summaryB.toLowerCase().contains(summaryA.toLowerCase().trim());
	}
}