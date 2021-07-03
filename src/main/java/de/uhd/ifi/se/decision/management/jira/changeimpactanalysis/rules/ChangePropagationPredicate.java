package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules;

import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.ChangePropagationRule;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.Link;

/**
 * Decides whether a change impact is propagated to the next
 * {@link KnowledgeElement} in the {@link KnowledgeGraph} or not using a certain
 * rule (for rule-based change impact estimation/analysis).
 * 
 * @see ChangePropagationRule
 * @see StopAtSameElementType
 * @see IgnoreIncomingLinks
 */
public interface ChangePropagationPredicate {

	/**
	 * @param filterSettings
	 *            including the selected element that is changed (see
	 *            {@link FilterSettings#getSelectedElement()}.
	 * @param currentElement
	 *            current {@link KnowledgeElement} in the {@link KnowledgeGraph}
	 *            that is traversed and that is affected by the change.
	 * @param link
	 *            {@link Link} (i.e. edge/relationship in the
	 *            {@link KnowledgeGraph}) that is traversed from the current element
	 *            to the next element.
	 * @return true if the change should be propagated. If false, the change impact
	 *         of the next element will be set to 0.
	 */
	boolean isChangePropagated(FilterSettings filterSettings, KnowledgeElement currentElement, Link link);
}
