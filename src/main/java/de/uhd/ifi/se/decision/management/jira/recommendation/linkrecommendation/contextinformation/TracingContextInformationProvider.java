package de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.recommendation.RecommendationScore;

/**
 * Uses existing tracing links between {@link KnowledgeElement}s for rating a
 * relation. This provider assumes that a {@link KnowledgeElement} that traces
 * to another element has a close relation to this element (Miesbauer and
 * Weinreich, 2012).
 */
public class TracingContextInformationProvider extends ContextInformationProvider {

	/**
	 * Per default, this context information provider is deactivated.
	 */
	public TracingContextInformationProvider() {
		super();
		isActive = false;
	}

	@Override
	public RecommendationScore assessRelation(KnowledgeElement baseElement, KnowledgeElement knowledgeElement) {
		float value = 1;
		if (!baseElement.equals(knowledgeElement)) {
			int distance = baseElement.getLinkDistance(knowledgeElement, 5);
			// Prevent a division by zero exception
			// Unlinked elements (with distance -1) get the highest score
			value = 1.0f / (distance + 2);
		}
		return new RecommendationScore(value, getDescription());
	}

	@Override
	public String getExplanation() {
		return "Assumes that knowledge elements that can be traced within a certain number of hops are related.";
	}

	@Override
	public String getDescription() {
		return "Recommend elements that can be traced to the source element";
	}
}
