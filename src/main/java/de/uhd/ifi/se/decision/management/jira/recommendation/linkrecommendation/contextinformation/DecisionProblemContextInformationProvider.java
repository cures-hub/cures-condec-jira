package de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.recommendation.RecommendationScore;

/**
 * Rates relations based on their assignment to a specific knowledge type.
 * Recommends elements that are assigned to the knowledge type 'decision
 * problem'.
 */
public class DecisionProblemContextInformationProvider extends ContextInformationProvider {

	/**
	 * Per default, this context information provider is activated. Thus, knowledge
	 * elements that are decision problems (issues) are more likely to be
	 * recommended.
	 */
	public DecisionProblemContextInformationProvider() {
		super();
		isActive = true;
	}

	@Override
	public RecommendationScore assessRelation(KnowledgeElement baseElement, KnowledgeElement otherElement) {
		if (otherElement.getType().getSuperType().equals(KnowledgeType.PROBLEM)) {
			return new RecommendationScore(1.0f, getName());
		}
		return new RecommendationScore(0.0f, getName());
	}

	@Override
	public String getExplanation() {
		return "Recommends knowledge elements that are decision problems.";
	}

	@Override
	public String getDescription() {
		return "Recommend elements that are decision problems.";
	}
}
