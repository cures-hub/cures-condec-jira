package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import java.util.Set;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

/**
 * Checks whether a given {@link KnowledgeElement} is documented completely
 * according to the definition of done.
 * 
 * For example, an argument needs to be linked to at least one solution option
 * (decision or alternative) in the {@link KnowledgeGraph}. Otherwise, it is
 * incomplete, i.e., its documentation needs to be improved.
 */
public abstract class KnowledgeElementCompletenessCheck {

	protected KnowledgeElement knowledgeElement;
	protected KnowledgeGraph graph;
	protected Set<KnowledgeElement> neighbours;
	protected String projectKey;

	abstract protected boolean isCompleteAccordingToDefault();

	abstract protected boolean isCompleteAccordingToSettings();

	protected boolean hasNeighbourOfType(KnowledgeType knowledgeType) {
		for (KnowledgeElement knowledgeElement : neighbours) {
			if (knowledgeElement.getType() == knowledgeType)
				return true;
		}
		return false;
	}

}
