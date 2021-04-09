package de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.KnowledgeSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.Recommendation;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.RecommendationScore;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.Recommender;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluation.metrics.AveragePrecision;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluation.metrics.EvaluationMetric;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluation.metrics.FScore;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluation.metrics.NumberOfTruePositives;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluation.metrics.Precision;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluation.metrics.Recall;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluation.metrics.ReciprocalRank;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;

/**
 * Evaluates one {@link KnowledgeSource} for a given decision problem and
 * keywords. Creates a {@link RecommendationEvaluation} object as output.
 */
public class Evaluator {

	/**
	 * @param decisionProblem
	 * @param keywords
	 * @param topKResults
	 * @param knowledgeSource
	 *            {@link KnowledgeSource}
	 * @return {@link RecommendationEvaluation} that contains the evaluation metrics
	 *         for one {@link KnowledgeSource} for a given decision problem and
	 *         keywords.
	 */
	public static RecommendationEvaluation evaluate(KnowledgeElement decisionProblem, String keywords, int topKResults,
			KnowledgeSource knowledgeSource) {
		String projectKey = decisionProblem.getProject().getProjectKey();
		List<Recommendation> recommendationsFromKnowledgeSource = Recommender.newInstance(projectKey, knowledgeSource)
				.getRecommendations(keywords, decisionProblem);

		List<KnowledgeElement> solutionOptions = decisionProblem.getLinkedSolutionOptions();
		recommendationsFromKnowledgeSource
				.sort(Comparator.comparingDouble(recommendation -> recommendation.getScore().getValue()));
		Collections.reverse(recommendationsFromKnowledgeSource);

		List<Recommendation> topKRecommendations = Evaluator.getTopKRecommendations(recommendationsFromKnowledgeSource,
				topKResults);
		List<EvaluationMetric> metrics = calculateMetrics(topKRecommendations, solutionOptions);
		return new RecommendationEvaluation(knowledgeSource, recommendationsFromKnowledgeSource, metrics,
				solutionOptions);
	}

	/**
	 * @return {@link RecommendationEvaluation} that contains the evaluation metrics
	 *         for one {@link KnowledgeSource} for a given decision problem and
	 *         keywords.
	 */
	public static RecommendationEvaluation evaluate(KnowledgeElement decisionProblem, String keywords, int topKResults,
			String knowledgeSourceName) {
		KnowledgeSource knowledgeSource = getKnowledgeSource(decisionProblem, knowledgeSourceName);
		return evaluate(decisionProblem, keywords, topKResults, knowledgeSource);
	}

	private static KnowledgeSource getKnowledgeSource(KnowledgeElement decisionProblem, String knowledgeSourceName) {
		return ConfigPersistenceManager.getDecisionGuidanceConfiguration(decisionProblem.getProject().getProjectKey())
				.getKnowledgeSourceByName(knowledgeSourceName);
	}

	/**
	 * @param recommendations
	 *            either all or top-k {@link Recommendation}s.
	 * @param groundTruthSolutionOptions
	 *            alternatives and decisions for a decision problem that are treated
	 *            as the ground truth.
	 * @return list of {@link EvaluationMetric}s such
	 */
	private static List<EvaluationMetric> calculateMetrics(List<Recommendation> recommendations,
			List<KnowledgeElement> groundTruthSolutionOptions) {
		List<EvaluationMetric> metrics = new ArrayList<>();
		metrics.add(new NumberOfTruePositives(recommendations, groundTruthSolutionOptions));
		metrics.add(new FScore(recommendations, groundTruthSolutionOptions));
		metrics.add(new ReciprocalRank(recommendations, groundTruthSolutionOptions));
		metrics.add(new Precision(recommendations, groundTruthSolutionOptions));
		metrics.add(new Recall(recommendations, groundTruthSolutionOptions));
		metrics.add(new AveragePrecision(recommendations, groundTruthSolutionOptions));
		return metrics;
	}

	/**
	 * @param allRecommendations
	 *            all {@link Recommendation}s sorted by their
	 *            {@link RecommendationScore}.
	 * @param k
	 *            number of {@link Recommendation}s with the highest
	 *            {@link RecommendationScore} that should be included in the
	 *            evaluation. All other recommendations are ignored.
	 * @return the top-k {@link Recommendation}s with the hightest
	 *         {@link RecommendationScore}s.
	 */
	public static List<Recommendation> getTopKRecommendations(List<Recommendation> allRecommendations, int k) {
		if (k <= 0 || k >= allRecommendations.size()) {
			return allRecommendations;
		}
		return allRecommendations.subList(0, k);
	}
}
