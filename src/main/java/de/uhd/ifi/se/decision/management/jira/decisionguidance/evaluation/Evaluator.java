package de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Nonnull;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.KnowledgeSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.Recommendation;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.RecommendationScore;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.RecommenderFactory;
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
 * Evaluates one ore more {@link KnowledgeSource}s for given inputs. Creates
 * {@link RecommendationEvaluation}s as output.
 */
public class Evaluator {

	private KnowledgeElement decisionProblem;
	private String keywords;
	private int topKResults;
	protected List<Recommendation> recommendations;
	protected KnowledgeSource knowledgeSource;

	public Evaluator(KnowledgeElement decisionProblem, String keywords, int topKResults,
			KnowledgeSource knowledgeSource) {
		this.recommendations = new ArrayList<>();
		this.knowledgeSource = knowledgeSource;
		this.decisionProblem = decisionProblem;
		this.keywords = keywords;
		this.topKResults = topKResults;
	}

	public Evaluator(KnowledgeElement decisionProblem, String keywords, int topKResults, String knowledgeSourceName) {
		this(decisionProblem, keywords, topKResults, getKnowledgeSource(decisionProblem, knowledgeSourceName));
	}

	private static KnowledgeSource getKnowledgeSource(KnowledgeElement decisionProblem, String knowledgeSourceName) {
		return ConfigPersistenceManager.getDecisionGuidanceConfiguration(decisionProblem.getProject().getProjectKey())
				.getKnowledgeSourceByName(knowledgeSourceName);
	}

	public Evaluator evaluate(@Nonnull KnowledgeElement issue) {
		this.decisionProblem = issue;
		return this;
	}

	public RecommendationEvaluation execute() {
		String projectKey = decisionProblem.getProject().getProjectKey();
		List<Recommendation> recommendationsFromKnowledgeSource = RecommenderFactory
				.getRecommender(projectKey, knowledgeSource).getRecommendations(keywords, decisionProblem);

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

	public KnowledgeElement getKnowledgeElement() {
		return decisionProblem;
	}

	public void setKnowledgeElement(KnowledgeElement knowledgeElement) {
		this.decisionProblem = knowledgeElement;
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
