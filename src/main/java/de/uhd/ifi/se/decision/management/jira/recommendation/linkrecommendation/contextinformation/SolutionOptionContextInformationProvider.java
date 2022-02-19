package de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.recommendation.RecommendationScore;

/**
 * Rates relations based on their assignment to a specific knowledge type.
 * Recommends elements that are assigned to the knowledge type 'solution
 * option'.
 */
public class SolutionOptionContextInformationProvider extends ContextInformationProvider {

	/**
	 * Per default, this context information provider is activated. Thus, knowledge
	 * elements that are alternatives or decisions are more likely to be
	 * recommended.
	 */
	public SolutionOptionContextInformationProvider() {
		super();
		isActive = true;
	}

	@Override
	public RecommendationScore assessRelation(KnowledgeElement baseElement, KnowledgeElement otherElement) {
		if (otherElement.getType().getSuperType().equals(KnowledgeType.SOLUTION)) {
			return new RecommendationScore(1.0f, getName());
		}
		return new RecommendationScore(0.0f, getName());
	}

	@Override
	public String getExplanation() {
		return "Recommends knowledge elements that are solution options.";
	}

	@Override
	public String getDescription() {
		return "Recommend elements that are solution options.";
	}
}
