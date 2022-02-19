package de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation;

import java.util.Set;
import java.util.stream.Collectors;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.recommendation.RecommendationScore;

/**
 * Rates relations based on the assignment to specific decision groups. Elements
 * that are assigned to the same decision group are stronger related than
 * elements that are not.
 */
public class DecisionGroupContextInformationProvider extends ContextInformationProvider {

	/**
	 * Per default, this context information provider is activated. Thus, knowledge
	 * elements that belong to the same decision group are more likely to be
	 * recommended.
	 */
	public DecisionGroupContextInformationProvider() {
		super();
		isActive = true;
	}

	@Override
	public RecommendationScore assessRelation(KnowledgeElement baseElement, KnowledgeElement otherElement) {
		float score = 1;
		int numberOfGroupsOfBaseElement = baseElement.getDecisionGroups().size();
		if (numberOfGroupsOfBaseElement > 0) {
			Set<String> setOfMatchingDecisionGroups = baseElement.getDecisionGroups().stream()
					.filter(item -> otherElement.getDecisionGroups().contains(item)).collect(Collectors.toSet());
			score = setOfMatchingDecisionGroups.size() / numberOfGroupsOfBaseElement;
		}
		return new RecommendationScore(score, getName());
	}

	@Override
	public String getExplanation() {
		return "Assumes that knowledge elements belonging the same decision groups/level are related.";
	}

	@Override
	public String getDescription() {
		return "Recommend elements that are assigned to the same decision group as the source element.";
	}
}
