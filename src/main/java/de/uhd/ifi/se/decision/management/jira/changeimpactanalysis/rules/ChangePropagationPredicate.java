package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules;

import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.ChangePropagationRule;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.Link;

/**
 * Decides whether a change impact is propagated to the next
 * {@link KnowledgeElement} in the {@link KnowledgeGraph} or not using a certain
 * rule (for rule-based change impact estimation/analysis).
 * 
 * @see ChangePropagationRule
 * @see IgnoreArgumentsRule
 * @see IgnoreDecisionIncoming
 */
public interface ChangePropagationPredicate {

	boolean isChangePropagated(KnowledgeElement root, double srcImpact, KnowledgeElement next, double destImpact, Link link);
}
