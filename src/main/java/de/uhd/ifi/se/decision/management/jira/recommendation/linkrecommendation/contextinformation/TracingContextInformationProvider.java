package de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.recommendation.RecommendationScore;

/**
 * Uses existing tracing links between {@link KnowledgeElement}s for rating a
 * relation. This provider assumes that a {@link KnowledgeElement} that traces
 * to another element has a close relation to this element. (Miesbauer and
 * Weinreich, 2012)
 */
public class TracingContextInformationProvider implements ContextInformationProvider {

	@Override
	public RecommendationScore assessRelation(KnowledgeElement baseElement, KnowledgeElement knowledgeElement) {
		int distance = baseElement.getLinkDistance(knowledgeElement, 5);
		// A null value means the nodes are not connected.
		double value = 0.;
		value = 1. / (distance + 1);
		// Prevent a division by zero exception.
		return new RecommendationScore((float) value, getName());
	}
}
