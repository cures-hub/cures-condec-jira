package de.uhd.ifi.se.decision.management.jira.decisionguidance.recommender;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluationframework.evaluationmethods.*;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.KnowledgeSource;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.RecommendationEvaluation;
import weka.core.pmml.jaxbbindings.True;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

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
	public List<Recommendation> getResultFromKnowledgeSource(KnowledgeSource knowledgeSource) {
		return knowledgeSource.getResults(this.knowledgeElement);
	}

	public EvaluationRecommender evaluate(@Nonnull KnowledgeElement issue) {
		this.knowledgeElement = issue;
		return this;
	}

	public RecommendationEvaluation execute() {

		List<Recommendation> recommendationsFromKnowledgeSource;
		RecommenderType recommenderType = RecommenderType.ISSUE;
		if (!keywords.isBlank()) {
			this.knowledgeSources.get(0).setRecommenderType(RecommenderType.KEYWORD);
			recommendationsFromKnowledgeSource = this.knowledgeSources.get(0).getResults(this.keywords);
			recommenderType = RecommenderType.KEYWORD;
		} else {
			this.knowledgeSources.get(0).setRecommenderType(RecommenderType.ISSUE);
			recommendationsFromKnowledgeSource = this.knowledgeSources.get(0).getResults(this.knowledgeElement);
		}


		recommendationsFromKnowledgeSource.sort(Comparator.comparingInt(Recommendation::getScore));
		Collections.reverse(recommendationsFromKnowledgeSource);

		List<KnowledgeElement> alternatives = this.knowledgeElement.getLinks().stream()
			.filter(link -> link.getSource().getType().equals(KnowledgeType.ALTERNATIVE)).collect(Collectors.toList()).stream()
			.map(Link::getSource)
			.collect(Collectors.toList());

		List<KnowledgeElement> decisions = this.knowledgeElement.getLinks().stream()
			.filter(link -> link.getSource().getType().equals(KnowledgeType.DECISION)).collect(Collectors.toList()).stream()
			.map(Link::getSource)
			.collect(Collectors.toList());


		List<KnowledgeElement> ideas = this.getElementsWithStatus(alternatives, KnowledgeStatus.IDEA);
		List<KnowledgeElement> discarded = this.getElementsWithStatus(alternatives, KnowledgeStatus.DISCARDED);

		List<KnowledgeElement> decided = this.getElementsWithStatus(decisions, KnowledgeStatus.DECIDED);
		List<KnowledgeElement> rejected = this.getElementsWithStatus(decisions, KnowledgeStatus.REJECTED);


		List<KnowledgeElement> solutionOptions = new ArrayList<>();
		solutionOptions.addAll(ideas);
		solutionOptions.addAll(discarded);
		solutionOptions.addAll(decided);
		solutionOptions.addAll(rejected);


		List<EvaluationMethod> metrics = new ArrayList<>();
		metrics.add(new FScore(recommendationsFromKnowledgeSource, solutionOptions, topKResults));
		metrics.add(new ReciprocalRank(recommendationsFromKnowledgeSource, solutionOptions, topKResults));
		metrics.add(new AveragePrecision(recommendationsFromKnowledgeSource, solutionOptions, topKResults));
		metrics.add(new TruePositives(recommendationsFromKnowledgeSource, solutionOptions, topKResults));

		return new RecommendationEvaluation(recommenderType.toString(), this.knowledgeSources.get(0).getName(), recommendationsFromKnowledgeSource.size(), metrics);
	}


	/**
	 * @param knowledgeElements
	 * @param status
	 * @return a list of elements with a given status
	 */
	public List<KnowledgeElement> getElementsWithStatus(List<KnowledgeElement> knowledgeElements, KnowledgeStatus status) {
		if (knowledgeElements == null) return new ArrayList<>();
		return knowledgeElements.stream().filter(element -> element.getStatus().equals(status)).collect(Collectors.toList());
	}


	/**
	 * Checks if the knowledge source exists and activates it
	 *
	 * @param knowledgeSources
	 * @param knowledgeSourceName
	 * @return
	 */
	public EvaluationRecommender withKnowledgeSource(List<? extends KnowledgeSource> knowledgeSources, String knowledgeSourceName) {
		for (KnowledgeSource knowledgeSource : knowledgeSources) {
			if (knowledgeSource.getName().equalsIgnoreCase(knowledgeSourceName.trim())) {
				knowledgeSource.setActivated(true);
				this.addKnowledgeSource(knowledgeSource);
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
}
