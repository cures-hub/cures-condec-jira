package de.uhd.ifi.se.decision.management.jira.decisionguidance.recommender;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.KnowledgeSource;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.RecommendationEvaluation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class IssueBasedRecommender extends BaseRecommender<KnowledgeElement> {

	private KnowledgeElement knowledgeElement;

	public IssueBasedRecommender(KnowledgeElement knowledgeElement) {
		this.recommendations = new ArrayList<>();
		this.knowledgeSources = new ArrayList<>();
		this.knowledgeElement = knowledgeElement;
	}

	public IssueBasedRecommender(KnowledgeElement knowledgeElement, List<KnowledgeSource> knowledgeSources) {
		this(knowledgeElement);
		this.addKnowledgeSource(knowledgeSources);
	}

	@Override
	public List<Recommendation> getRecommendation() {
		for (KnowledgeSource knowledgeSource : this.knowledgeSources) {
			this.recommendations.addAll(knowledgeSource.getResults(this.knowledgeElement));
		}
		return this.recommendations;
	}

	@Override
	public BaseRecommender evaluate(KnowledgeElement issue) {
		this.knowledgeElement = issue;
		return this;
	}

	@Override
	public RecommendationEvaluation execute() {

		List<Recommendation> recommendationsFromKnowledgeSource = this.knowledgeSources.get(0).getResults(this.knowledgeElement);


		List<KnowledgeElement> alternatives = this.knowledgeElement.getLinks().stream()
			.filter(link -> link.getSource().getType().equals(KnowledgeType.ALTERNATIVE)).collect(Collectors.toList()).stream()
			.map(link -> link.getSource())
			.collect(Collectors.toList());

		List<KnowledgeElement> decisions = this.knowledgeElement.getLinks().stream()
			.filter(link -> link.getSource().getType().equals(KnowledgeType.DECISION)).collect(Collectors.toList()).stream()
			.map(link -> link.getSource())
			.collect(Collectors.toList());


		List<KnowledgeElement> ideas = this.getElementsWithStatus(alternatives, KnowledgeStatus.IDEA);
		List<KnowledgeElement> discarded = this.getElementsWithStatus(alternatives, KnowledgeStatus.DISCARDED);

		List<KnowledgeElement> decided = this.getElementsWithStatus(decisions, KnowledgeStatus.DECIDED);
		List<KnowledgeElement> rejected = this.getElementsWithStatus(decisions, KnowledgeStatus.REJECTED);


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

		if (Double.isNaN(fScore)) fScore = 0.0;

		RecommendationEvaluation recommendationEvaluation = new RecommendationEvaluation(RecommenderType.ISSUE.toString(), this.knowledgeSources.get(0).getName(), recommendationsFromKnowledgeSource.size(), fScore);


		return recommendationEvaluation;
	}

	private List<KnowledgeElement> getElementsWithStatus(List<KnowledgeElement> knowledgeElements, KnowledgeStatus status) {
		return knowledgeElements.stream().filter(element -> element.getStatus().equals(status)).collect(Collectors.toList());
	}

	private int countIntersections(List<KnowledgeElement> knowledgeElements, String matchingString) {
		int counter = 0;
		for (KnowledgeElement knowledgeElement : knowledgeElements) {
			if (knowledgeElement.getSummary().trim().equals(matchingString.trim()))
				counter += 1;
		}
		return counter;
	}
}
