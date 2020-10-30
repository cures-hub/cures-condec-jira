package de.uhd.ifi.se.decision.management.jira.decisionguidance.recommender;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.KnowledgeSource;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.RecommendationEvaluation;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class EvaluationRecommender extends BaseRecommender<KnowledgeElement> {

	private KnowledgeElement knowledgeElement;
	private final String keywords;

	public EvaluationRecommender(KnowledgeElement knowledgeElement, String keywords) {
		this.recommendations = new ArrayList<>();
		this.knowledgeSources = new ArrayList<>();
		this.knowledgeElement = knowledgeElement;
		this.keywords = keywords;
	}

	public EvaluationRecommender(KnowledgeElement knowledgeElement, String keywords, List<KnowledgeSource> knowledgeSources) {
		this(knowledgeElement, keywords);
		this.addKnowledgeSource(knowledgeSources);
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
			recommendationsFromKnowledgeSource = this.knowledgeSources.get(0).getResults(this.keywords);
			recommenderType = RecommenderType.KEYWORD;
		} else {
			recommendationsFromKnowledgeSource = this.knowledgeSources.get(0).getResults(this.knowledgeElement);
		}


		recommendationsFromKnowledgeSource.sort(Comparator.comparingInt(Recommendation::getScore));


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

		double mrr = this.calculateMRRForRecommendations(recommendationsFromKnowledgeSource, solutionOptions);


		int intersectingIdeas = 0;
		int intersectingDiscarded = 0;
		int intersectedDecided = 0;
		int intersectedRejected = 0;

		for (Recommendation recommendation : recommendationsFromKnowledgeSource) {
			intersectingIdeas += this.countIntersections(ideas, recommendation.getRecommendations());
			intersectingDiscarded += this.countIntersections(discarded, recommendation.getRecommendations());
			intersectedDecided += this.countIntersections(decided, recommendation.getRecommendations());
			intersectedRejected += this.countIntersections(rejected, recommendation.getRecommendations());
		}

		int falseNegative = (ideas.size() + discarded.size() + decided.size() + rejected.size()) - intersectingIdeas - intersectingDiscarded - intersectedDecided - intersectedRejected;


		double fScore = this.calculateFScore(intersectingIdeas + intersectedDecided, falseNegative, intersectingDiscarded + intersectedRejected);


		return new RecommendationEvaluation(recommenderType.toString(), this.knowledgeSources.get(0).getName(), recommendationsFromKnowledgeSource.size(), fScore, mrr);
	}

	/**
	 * @param recommendations
	 * @return
	 */
	public double calculateMRRForRecommendations(List<Recommendation> recommendations, List<KnowledgeElement> solutionOptions) {
		double sum_RR = 0.0;

		for (int i = 0; i < recommendations.size(); i++) {
			for (KnowledgeElement solutionOption : solutionOptions) {
				if (solutionOption.getSummary().trim().equals(recommendations.get(i).getRecommendations().trim())) {
					sum_RR += (1.0 / (i + 1));
				}
			}
		}
		double MRR = sum_RR / recommendations.size();
		return !Double.isNaN(MRR) ? MRR : 0.0;
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
	 * count intersections between the summary of a knowledge elements and a given string
	 *
	 * @param knowledgeElements
	 * @param matchingString
	 * @return
	 */
	public int countIntersections(List<KnowledgeElement> knowledgeElements, String matchingString) {
		int counter = 0;
		for (KnowledgeElement knowledgeElement : knowledgeElements) {
			if (knowledgeElement.getSummary().trim().equalsIgnoreCase(matchingString.trim()))
				counter += 1;
		}
		return counter;
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
			if (knowledgeSource.getName().equals(knowledgeSourceName)) {
				knowledgeSource.setActivated(true);
				this.addKnowledgeSource(knowledgeSource);
			}
		}
		return this;
	}

	public double calculateFScore(int truePositive, int falseNegative, int falsePositive) {
		double fScore = truePositive / (truePositive + .5 * (falsePositive + falseNegative));
		if (Double.isNaN(fScore)) fScore = 0.0;
		return fScore;
	}

	public double calculateFScore(double precision, double recall) {
		return 2 * ((precision * recall) / (precision + recall));
	}

	public KnowledgeElement getKnowledgeElement() {
		return knowledgeElement;
	}

	public void setKnowledgeElement(KnowledgeElement knowledgeElement) {
		this.knowledgeElement = knowledgeElement;
	}

	public String getKeywords() {
		return keywords;
	}
}
