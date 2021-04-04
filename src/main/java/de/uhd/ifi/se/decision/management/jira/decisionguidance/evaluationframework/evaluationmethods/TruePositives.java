package de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluationframework.evaluationmethods;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.Recommendation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;

import java.util.List;

public class TruePositives extends EvaluationMethod {

	public TruePositives(List<Recommendation> recommendations, List<KnowledgeElement> solutionOptions, int topKResults) {
		this.recommendations = recommendations;
		this.solutionOptions = solutionOptions;
		this.topKResults = topKResults;
	}

	@Override
	public double calculateMetric() {


		int intersectingIdeas = 0;
		int intersectedDecided = 0;

		for (Recommendation recommendation : recommendations) {
			intersectingIdeas += this.countIntersections(solutionOptions, recommendation.getSummary(), KnowledgeStatus.IDEA);
			intersectedDecided += this.countIntersections(solutionOptions, recommendation.getSummary(), KnowledgeStatus.DECIDED);
		}

		int truePositive = intersectingIdeas + intersectedDecided;
		return truePositive;
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

	@Override
	public String getName() {
		return "True Positive";
	}

	@Override
	public String getDescription() {
		return "Gives the number of correct recommended results.";
	}


}
