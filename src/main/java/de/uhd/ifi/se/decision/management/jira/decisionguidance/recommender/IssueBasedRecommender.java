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


		List<KnowledgeElement> ideas = alternatives.stream().filter(alternative -> alternative.getStatus().equals(KnowledgeStatus.IDEA)).collect(Collectors.toList());
		List<KnowledgeElement> discarded = alternatives.stream().filter(alternative -> alternative.getStatus().equals(KnowledgeStatus.DISCARDED)).collect(Collectors.toList());


		int intersectingIdeas = 0;
		int intersectingDiscarded = 0;

		for (Recommendation recommendation : recommendationsFromKnowledgeSource) {

			for (KnowledgeElement idea : ideas) {
				if (idea.getSummary().trim().equals(recommendation.getRecommendations().trim()))
					intersectingIdeas += 1;
			}

			for (KnowledgeElement discard : discarded) {
				if (discard.getSummary().trim().equals(recommendation.getRecommendations().trim()))
					intersectingDiscarded += 1;
			}
		}

		int falseNegative = (ideas.size() + discarded.size()) - intersectingIdeas - intersectingDiscarded;


		double fScore = this.calculateFScore(intersectingIdeas, falseNegative, intersectingDiscarded);

		if (Double.isNaN(fScore)) fScore = 0.0;

		RecommendationEvaluation recommendationEvaluation = new RecommendationEvaluation(RecommenderType.ISSUE.toString(), this.knowledgeSources.get(0).getName(), recommendationsFromKnowledgeSource.size(), fScore);


		return recommendationEvaluation;
	}
}
