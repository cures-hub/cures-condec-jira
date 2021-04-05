package de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluation.metrics;

import java.util.List;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.Recommendation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;

public class FScore extends EvaluationMetric {

	public FScore(List<Recommendation> recommendations, List<KnowledgeElement> solutionOptions, int topKResults) {
		super(recommendations, solutionOptions, topKResults);
	}

	@Override
	public double calculateMetric() {
		int intersectingIdeas = 0;
		int intersectingDiscarded = 0;
		int intersectedDecided = 0;
		int intersectedRejected = 0;

		for (Recommendation recommendation : recommendations) {
			intersectingIdeas += this.countIntersections(groundTruthSolutionOptions, recommendation.getSummary(),
					KnowledgeStatus.IDEA);
			intersectingDiscarded += this.countIntersections(groundTruthSolutionOptions, recommendation.getSummary(),
					KnowledgeStatus.DISCARDED);
			intersectedDecided += this.countIntersections(groundTruthSolutionOptions, recommendation.getSummary(),
					KnowledgeStatus.DECIDED);
			intersectedRejected += this.countIntersections(groundTruthSolutionOptions, recommendation.getSummary(),
					KnowledgeStatus.REJECTED);
		}

		int truePositive = intersectingIdeas + intersectedDecided;
		int falseNegative = (groundTruthSolutionOptions.size()) - intersectingIdeas - intersectingDiscarded
				- intersectedDecided - intersectedRejected;
		int falsePositive = intersectingDiscarded + intersectedRejected;

		double fScore = truePositive / (truePositive + .5 * (falsePositive + falseNegative));
		if (Double.isNaN(fScore))
			fScore = 0.0;
		return fScore;
	}

	public int countIntersections(List<KnowledgeElement> knowledgeElements, String matchingString,
			KnowledgeStatus status) {
		int counter = 0;
		for (KnowledgeElement knowledgeElement : knowledgeElements) {
			if ((knowledgeElement.getSummary().toLowerCase().trim().contains(matchingString.toLowerCase().trim())
					|| matchingString.toLowerCase().trim().contains(knowledgeElement.getSummary().toLowerCase().trim()))
					&& knowledgeElement.getStatus().equals(status))
				counter += 1;
		}
		return counter;
	}

	@Override
	public String getName() {
		return "F-Score";
	}

	@Override
	public String getDescription() {
		return "F-Score";
	}
}