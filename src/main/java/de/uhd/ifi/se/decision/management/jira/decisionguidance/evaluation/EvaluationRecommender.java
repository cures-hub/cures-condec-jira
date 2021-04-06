package de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.Recommendation;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluation.metrics.AveragePrecision;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluation.metrics.EvaluationMetric;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluation.metrics.FScore;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluation.metrics.NumberOfTruePositives;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluation.metrics.Precision;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluation.metrics.Recall;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluation.metrics.ReciprocalRank;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.InputMethod;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.KnowledgeSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.recommender.BaseRecommender;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.recommender.RecommenderType;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;

public class EvaluationRecommender extends BaseRecommender<KnowledgeElement> {

	private KnowledgeElement knowledgeElement;
	private final String keywords;
	private int topKResults;

	public EvaluationRecommender(KnowledgeElement knowledgeElement, String keywords, int topKResults) {
		this.recommendations = new ArrayList<>();
		this.knowledgeSources = new ArrayList<>();
		this.knowledgeElement = knowledgeElement;
		this.keywords = keywords;
		this.topKResults = topKResults;
	}

	@Override
	public List<Recommendation> getRecommendations(KnowledgeSource knowledgeSource) {
		return InputMethod.getIssueBasedIn(knowledgeSource).getRecommendations(this.knowledgeElement);
	}

	public EvaluationRecommender evaluate(@Nonnull KnowledgeElement issue) {
		this.knowledgeElement = issue;
		return this;
	}

	public RecommendationEvaluation execute() {
		List<Recommendation> recommendationsFromKnowledgeSource;
		RecommenderType recommenderType = RecommenderType.ISSUE;
		if (!keywords.isBlank()) {
			recommendationsFromKnowledgeSource = InputMethod.getKeywordBasedIn(knowledgeSources.get(0))
					.getRecommendations(this.keywords);
			recommenderType = RecommenderType.KEYWORD;
		} else {
			recommendationsFromKnowledgeSource = InputMethod.getIssueBasedIn(knowledgeSources.get(0))
					.getRecommendations(this.knowledgeElement);
		}

		recommendationsFromKnowledgeSource
				.sort(Comparator.comparingDouble(recommendation -> recommendation.getScore().getValue()));
		Collections.reverse(recommendationsFromKnowledgeSource);

		List<KnowledgeElement> alternatives = knowledgeElement.getLinks().stream()
				.filter(link -> link.getSource().getType() == KnowledgeType.ALTERNATIVE).collect(Collectors.toList())
				.stream().map(Link::getSource).collect(Collectors.toList());

		List<KnowledgeElement> decisions = knowledgeElement.getLinks().stream()
				.filter(link -> link.getSource().getType() == KnowledgeType.DECISION).collect(Collectors.toList())
				.stream().map(Link::getSource).collect(Collectors.toList());

		List<KnowledgeElement> ideas = this.getElementsWithStatus(alternatives, KnowledgeStatus.IDEA);
		List<KnowledgeElement> discarded = this.getElementsWithStatus(alternatives, KnowledgeStatus.DISCARDED);

		List<KnowledgeElement> decided = this.getElementsWithStatus(decisions, KnowledgeStatus.DECIDED);
		List<KnowledgeElement> rejected = this.getElementsWithStatus(decisions, KnowledgeStatus.REJECTED);

		List<KnowledgeElement> solutionOptions = new ArrayList<>();
		solutionOptions.addAll(ideas);
		solutionOptions.addAll(discarded);
		solutionOptions.addAll(decided);
		solutionOptions.addAll(rejected);

		List<Recommendation> topKRecommendations = EvaluationMetric
				.getTopKRecommendations(recommendationsFromKnowledgeSource, topKResults);

		List<EvaluationMetric> metrics = new ArrayList<>();
		metrics.add(new NumberOfTruePositives(topKRecommendations, solutionOptions));
		metrics.add(new FScore(topKRecommendations, solutionOptions));
		metrics.add(new ReciprocalRank(topKRecommendations, solutionOptions));
		metrics.add(new Precision(topKRecommendations, solutionOptions));
		metrics.add(new Recall(topKRecommendations, solutionOptions));
		metrics.add(new AveragePrecision(topKRecommendations, solutionOptions));

		return new RecommendationEvaluation(recommenderType, this.knowledgeSources.get(0),
				recommendationsFromKnowledgeSource.size(), metrics);
	}

	/**
	 * @param knowledgeElements
	 * @param status
	 * @return a list of elements with a given status
	 */
	public List<KnowledgeElement> getElementsWithStatus(List<KnowledgeElement> knowledgeElements,
			KnowledgeStatus status) {
		if (knowledgeElements == null)
			return new ArrayList<>();
		return knowledgeElements.stream().filter(element -> element.getStatus().equals(status))
				.collect(Collectors.toList());
	}

	/**
	 * Checks if the knowledge source exists and activates it
	 *
	 * @param knowledgeSources
	 * @param knowledgeSourceName
	 * @return
	 */
	public EvaluationRecommender withKnowledgeSource(List<? extends KnowledgeSource> knowledgeSources,
			String knowledgeSourceName) {
		for (KnowledgeSource knowledgeSource : knowledgeSources) {
			if (knowledgeSource.getName().equalsIgnoreCase(knowledgeSourceName.trim())) {
				knowledgeSource.setActivated(true);
				addKnowledgeSource(knowledgeSource);
			}
		}
		return this;
	}

	public KnowledgeElement getKnowledgeElement() {
		return knowledgeElement;
	}

	public void setKnowledgeElement(KnowledgeElement knowledgeElement) {
		this.knowledgeElement = knowledgeElement;
	}

	@Override
	public RecommenderType getRecommenderType() {
		return null;
	}
}
