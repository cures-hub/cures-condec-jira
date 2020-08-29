package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;

/**
 * Checks whether a given {@link KnowledgeElement} is documented completely
 * according to the {@link DefinitionOfDone}.
 *
 * For example, an argument needs to be linked to at least one solution option
 * (decision or alternative) in the {@link KnowledgeGraph}. Otherwise, it is
 * incomplete, i.e., its documentation needs to be improved.
 */
public interface CompletenessCheck {

	/**
	 * Executes the completeness check for the given knowledge element.
	 * 
	 * @param knowledgeElement
	 *            instance of {@link KnowledgeElement}.
	 * @return true if the element is completely documented according to the default
	 *         and configured rules of the {@link DefinitionOfDone}.
	 */
	boolean execute(KnowledgeElement knowledgeElement);

	boolean isCompleteAccordingToDefault();

	boolean isCompleteAccordingToSettings();
}
