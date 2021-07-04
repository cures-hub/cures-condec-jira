package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis;

import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules.BoostWhenTextualSimilar;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules.ChangePropagationFunction;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules.IgnoreIncomingLinks;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules.StopAtSameElementType;

/**
 * Gathers propagation rules for rule-based change impact analysis.
 * 
 * The implementing classes of {@link ChangePropagationFunction} encode the
 * rules.
 */
public enum ChangePropagationRule {

	STOP_AT_SAME_ELEMENT_TYPE("Stop at elements with the same type as the selected element",
			new StopAtSameElementType()), //
	IGNORE_INCOMING_LINKS("Outward links only", new IgnoreIncomingLinks()), //
	BOOST_WHEN_TEXTUAL_SIMILAR("Boost when element is textual similar to the selected element",
			new BoostWhenTextualSimilar());

	private String description;
	private ChangePropagationFunction function;

	private ChangePropagationRule(String description, ChangePropagationFunction predicate) {
		this.description = description;
		this.function = predicate;
	}

	/**
	 * @return description of the change propagation rule.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return method with a boolean return value that encodes the propagation rule.
	 * @see ChangePropagationFunction#isChangePropagated
	 */
	public ChangePropagationFunction getFunction() {
		return function;
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
