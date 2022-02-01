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
		isActive = true;
	}

    @Override
    public RecommendationScore assessRelation(KnowledgeElement baseElement, KnowledgeElement otherElement) {
        if (baseElement.getType() == otherElement.getType()) {
            return new RecommendationScore(1.0f, getName());
        }
        return new RecommendationScore(0.75f, getName());
    }
 
    @Override
	public String getExplanation() {
		return "Assumes that knowledge elements having the same knowledge type are related, e.g. decisions, arguments, code.";
	}
}
