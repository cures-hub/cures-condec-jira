package de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation;

import java.util.Set;
import java.util.stream.Collectors;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.recommendation.RecommendationScore;

/**
 * Rates relations based on their assignment to specific decision groups.
 * Elements that are assigned to the same decision group are stronger related
 * than elements that are not.
 */
public class DecisionGroupContextInformationProvider extends ContextInformationProvider {

	public DecisionGroupContextInformationProvider() {
		super();
		isActive = true;
	}

	@Override
	public RecommendationScore assessRelation(KnowledgeElement baseElement, KnowledgeElement otherElement) {
		if (!baseElement.getDecisionGroups().isEmpty()) {
			if (otherElement.getDecisionGroups().isEmpty()) {
				return new RecommendationScore(0.75f, getName());
			}
			Set<String> setOfMatchingDecisionGroups = baseElement.getDecisionGroups().stream()
					.filter(item -> otherElement.getDecisionGroups().contains(item)).collect(Collectors.toSet());
			double score = setOfMatchingDecisionGroups.isEmpty() ? 0.75 : 1.0;
			return new RecommendationScore((float) score, getName());
		}
		return new RecommendationScore(1.0f, getName());
	}

	@Override
	public String getExplanation() {
		return "Assumes that knowledge elements belonging the same decision groups/level are related.";
	}
}
