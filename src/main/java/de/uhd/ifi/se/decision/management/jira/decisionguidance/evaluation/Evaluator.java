package de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.Recommendation;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.RecommendationScore;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluation.metrics.AveragePrecision;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluation.metrics.EvaluationMetric;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluation.metrics.FScore;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluation.metrics.NumberOfTruePositives;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluation.metrics.Precision;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluation.metrics.Recall;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluation.metrics.ReciprocalRank;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.InputMethod;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.KnowledgeSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.recommender.RecommenderType;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
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

	public Evaluator(KnowledgeElement decisionProblem, String keywords, int topKResults, String knowledgeSourceName) {
		this(decisionProblem, keywords, topKResults,
				ConfigPersistenceManager.getDecisionGuidanceConfiguration(decisionProblem.getProject().getProjectKey())
						.getKnowledgeSourceByName(knowledgeSourceName));
	}

	public Evaluator(KnowledgeElement decisionProblem, String keywords, int topKResults,
			KnowledgeSource knowledgeSource) {
		this.recommendations = new ArrayList<>();
		this.knowledgeSource = knowledgeSource;
		this.decisionProblem = decisionProblem;
		this.keywords = keywords;
		this.topKResults = topKResults;
	}

	public List<Recommendation> getRecommendations(KnowledgeSource knowledgeSource) {
		return InputMethod.getIssueBasedIn(knowledgeSource).getRecommendations(this.decisionProblem);
	}

	public Evaluator evaluate(@Nonnull KnowledgeElement issue) {
		this.decisionProblem = issue;
		return this;
	}

	public RecommendationEvaluation execute() {
		List<Recommendation> recommendationsFromKnowledgeSource;
		RecommenderType recommenderType = RecommenderType.determineType(decisionProblem, keywords);
		if (!keywords.isBlank()) {
			recommendationsFromKnowledgeSource = InputMethod.getKeywordBasedIn(knowledgeSource)
					.getRecommendations(this.keywords);
		} else {
			recommendationsFromKnowledgeSource = InputMethod.getIssueBasedIn(knowledgeSource)
					.getRecommendations(this.decisionProblem);
		}
		List<KnowledgeElement> solutionOptions = getGroundTruthSolutionOptions(decisionProblem);
		recommendationsFromKnowledgeSource
				.sort(Comparator.comparingDouble(recommendation -> recommendation.getScore().getValue()));
		Collections.reverse(recommendationsFromKnowledgeSource);

		List<Recommendation> topKRecommendations = Evaluator.getTopKRecommendations(recommendationsFromKnowledgeSource,
				topKResults);
		List<EvaluationMetric> metrics = calculateMetrics(topKRecommendations, solutionOptions);
		return new RecommendationEvaluation(recommenderType, this.knowledgeSource, recommendationsFromKnowledgeSource,
				metrics, solutionOptions);

	}

	private static List<KnowledgeElement> getGroundTruthSolutionOptions(KnowledgeElement decisionProblem) {
		List<KnowledgeElement> alternatives = decisionProblem.getLinks().stream()
				.filter(link -> link.getSource().getType() == KnowledgeType.ALTERNATIVE).collect(Collectors.toList())
				.stream().map(Link::getSource).collect(Collectors.toList());

		List<KnowledgeElement> decisions = decisionProblem.getLinks().stream()
				.filter(link -> link.getSource().getType() == KnowledgeType.DECISION).collect(Collectors.toList())
				.stream().map(Link::getSource).collect(Collectors.toList());

		List<KnowledgeElement> ideas = getElementsWithStatus(alternatives, KnowledgeStatus.IDEA);
		List<KnowledgeElement> discarded = getElementsWithStatus(alternatives, KnowledgeStatus.DISCARDED);

		List<KnowledgeElement> decided = getElementsWithStatus(decisions, KnowledgeStatus.DECIDED);
		List<KnowledgeElement> rejected = getElementsWithStatus(decisions, KnowledgeStatus.REJECTED);

		List<KnowledgeElement> solutionOptions = new ArrayList<>();
		solutionOptions.addAll(ideas);
		solutionOptions.addAll(discarded);
		solutionOptions.addAll(decided);
		solutionOptions.addAll(rejected);
		return solutionOptions;
	}

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
	 * @param knowledgeElements
	 * @param status
	 * @return a list of elements with a given status
	 */
	public static List<KnowledgeElement> getElementsWithStatus(List<KnowledgeElement> knowledgeElements,
			KnowledgeStatus status) {
		if (knowledgeElements == null)
			return new ArrayList<>();
		return knowledgeElements.stream().filter(element -> element.getStatus().equals(status))
				.collect(Collectors.toList());
	}

	public KnowledgeElement getKnowledgeElement() {
		return decisionProblem;
	}

	public void setKnowledgeElement(KnowledgeElement knowledgeElement) {
		this.decisionProblem = knowledgeElement;
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
}
