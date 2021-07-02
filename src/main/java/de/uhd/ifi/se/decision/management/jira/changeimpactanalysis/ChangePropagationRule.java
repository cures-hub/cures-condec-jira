package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis;

import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules.ChangePropagationPredicate;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules.IgnoreArgumentsRule;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules.IgnoreDecisionIncoming;

/**
 * Gathers propagation rules for rule-based change impact analysis.
 * 
 * The implementing classes of {@link ChangePropagationPredicate} encode the
 * rules.
 */
public enum ChangePropagationRule {

	IGNORE_ARGUMENTS("Ignore arguments for solution options", new IgnoreArgumentsRule()), //
	IGNORE_DecisionIncoming("IgnoreDecisionIncoming", new IgnoreDecisionIncoming());

	private String description;
	private ChangePropagationPredicate predicate;

	private ChangePropagationRule(String description, ChangePropagationPredicate predicate) {
		this.description = description;
		this.predicate = predicate;
	}

	/**
	 * @return description of the change propagation rule.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return method with a boolean return value that encodes the propagation rule.
	 * @see ChangePropagationPredicate#isChangePropagated
	 */
	public ChangePropagationPredicate getPredicate() {
		return predicate;
	}

	/**
	 * @param propagationRuleName
	 *            title of the {@link ChangePropagationRule}.
	 * @return {@link ChangePropagationRule} object or null if unknown.
	 */
	public static ChangePropagationRule getPropagationRule(String propagationRuleName) {
		if (propagationRuleName == null || propagationRuleName.isEmpty()) {
			return null;
		}
		for (ChangePropagationRule rule : ChangePropagationRule.values()) {
			if (rule.getDescription().matches(propagationRuleName)) {
				return rule;
			}
		}
		return null;
	}
}
