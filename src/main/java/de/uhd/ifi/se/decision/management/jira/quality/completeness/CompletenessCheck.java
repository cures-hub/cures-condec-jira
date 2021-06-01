package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import de.uhd.ifi.se.decision.management.jira.git.model.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.recommendation.Recommendation;

/**
 * Checks whether a given {@link KnowledgeElement} is documented completely
 * according to the {@link DefinitionOfDone} (DoD).
 *
 * For example, an argument needs to be linked to at least one solution option
 * (decision or alternative) in the {@link KnowledgeGraph}. Otherwise, it is
 * incomplete, i.e., its documentation needs to be improved.
 * 
 * @param <T>
 *            {@link KnowledgeElement} class or a subclass, e.g.
 *            {@link ChangedFile}, {@link PartOfJiraIssueText}, or
 *            {@link Recommendation}.
 */
public interface CompletenessCheck<T extends KnowledgeElement> {

	/**
	 * Executes the completeness check for the given knowledge element.
	 * 
	 * @param <T>
	 *            {@link KnowledgeElement} class or a subclass, e.g.
	 *            {@link ChangedFile}, {@link PartOfJiraIssueText}, or
	 *            {@link Recommendation}.
	 * @param knowledgeElement
	 *            instance of {@link KnowledgeElement}.
	 * @return true if the element is completely documented according to the default
	 *         and configured rules of the {@link DefinitionOfDone}.
	 */
	boolean execute(T knowledgeElement);

	/**
	 * Checks the default rules that a knowledge element needs to fulfill to be
	 * completely documented. For example, a default rule is that each decision
	 * problem (=issue) needs to be linked to a decision to be complete.
	 * 
	 * @return true if all defaults rules are fulfilled.
	 */
	boolean isCompleteAccordingToDefault();

	/**
	 * Checks the configurable rules that a knowledge element needs to fulfill to be
	 * completely documented according to the {@link DefinitionOfDone}. For example,
	 * a configurable rule is that each decision needs to be linked to at least one
	 * pro-argument to be complete.
	 * 
	 * @return true if all configured rules are fulfilled.
	 */
	boolean isCompleteAccordingToSettings();
}
