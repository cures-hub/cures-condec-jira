package de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.recommendation.RecommendationScore;

/**
 * Rates relations based on their assignment to specific knowledge type.
 * Elements that are assigned to the same knowledge type are stronger related
 * than elements that are not.
 */
public class KnowledgeTypeContextInformationProvider extends ContextInformationProvider {

	public KnowledgeTypeContextInformationProvider() {
		super();
		isActive = false;
	}

	@Override
	public RecommendationScore assessRelation(KnowledgeElement baseElement, KnowledgeElement otherElement) {
		float score = 0;
		if (baseElement.getType() == otherElement.getType()) {
			score = 1;
		}
		return new RecommendationScore(score, getDescription());
	}

	@Override
	public String getExplanation() {
		return "Assumes that knowledge elements having the same knowledge type are related, e.g. decisions, arguments, code.";
	}

	@Override
	public String getDescription() {
		return "Recommend elements that are the same knowledge type as the source element";
	}
}
