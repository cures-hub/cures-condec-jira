package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;

/**
 * Checks whether a given {@link KnowledgeElement} is documented completely
 * according to the definition of done.
 *
 * For example, an argument needs to be linked to at least one solution option
 * (decision or alternative) in the {@link KnowledgeGraph}. Otherwise, it is
 * incomplete, i.e., its documentation needs to be improved.
 */

public interface CompletenessCheck {

	boolean execute(KnowledgeElement knowledgeElement);

	boolean isCompleteAccordingToDefault();

	boolean isCompleteAccordingToSettings();
}
