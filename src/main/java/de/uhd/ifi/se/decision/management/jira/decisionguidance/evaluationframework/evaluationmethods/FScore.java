package de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluationframework.evaluationmethods;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FScore implements EvaluationMethod {

	@Override
	public double calculateMetric(List<Recommendation> recommendations, List<KnowledgeElement> solutionOptions, int topKResults) {


		int intersectingIdeas = 0;
		int intersectingDiscarded = 0;
		int intersectedDecided = 0;
		int intersectedRejected = 0;

		for (Recommendation recommendation : recommendations) {
			intersectingIdeas += this.countIntersections(solutionOptions, recommendation.getRecommendation(), KnowledgeStatus.IDEA);
			intersectingDiscarded += this.countIntersections(solutionOptions, recommendation.getRecommendation(), KnowledgeStatus.DISCARDED);
			intersectedDecided += this.countIntersections(solutionOptions, recommendation.getRecommendation(), KnowledgeStatus.DECIDED);
			intersectedRejected += this.countIntersections(solutionOptions, recommendation.getRecommendation(), KnowledgeStatus.REJECTED);
		}

		int truePositive = intersectingIdeas + intersectedDecided;
		int falseNegative = (solutionOptions.size()) - intersectingIdeas - intersectingDiscarded - intersectedDecided - intersectedRejected;
		int falsePositive = intersectingDiscarded + intersectedRejected;

		double fScore = truePositive / (truePositive + .5 * (falsePositive + falseNegative));
		if (Double.isNaN(fScore)) fScore = 0.0;
		return fScore;
	}


	public int countIntersections(List<KnowledgeElement> knowledgeElements, String matchingString, KnowledgeStatus status) {
		int counter = 0;
		for (KnowledgeElement knowledgeElement : knowledgeElements) {
			if ((knowledgeElement.getSummary().toLowerCase().trim().contains(matchingString.toLowerCase().trim()) ||
				matchingString.toLowerCase().trim().contains(knowledgeElement.getSummary().toLowerCase().trim()))
				&& knowledgeElement.getStatus().equals(status))
				counter += 1;
		}
		return counter;
	}

	public List<KnowledgeElement> getElementsWithStatus(List<KnowledgeElement> knowledgeElements, KnowledgeStatus status) {
		if (knowledgeElements == null) return new ArrayList<>();
		return knowledgeElements.stream().filter(element -> element.getStatus().equals(status)).collect(Collectors.toList());
	}

	@Override
	public String toString() {
		return "F-Score";
	}


}
