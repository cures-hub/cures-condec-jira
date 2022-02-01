package de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.recommendation.RecommendationScore;

/**
 * Rates relations based on their assignment to a specific knowledge type.
 * Recommends elements that are assigned to the knowledge type 'solution option'.
 */
public class SolutionOptionContextInformationProvider extends ContextInformationProvider {

    public SolutionOptionContextInformationProvider() {
		super();
		isActive = true;
	}

    @Override
    public RecommendationScore assessRelation(KnowledgeElement baseElement, KnowledgeElement otherElement) {
        if (otherElement.getType().getSuperType().equals(KnowledgeType.SOLUTION)) {
            return new RecommendationScore(1.0f, getName());
        }
        return new RecommendationScore(0.75f, getName());
    }

    @Override
	public String getExplanation() {
		return "Recommends knowledge elements that are solution options.";
	}    
}
