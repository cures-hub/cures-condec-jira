package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis;

import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules.BoostWhenHighAmountOfDistinctAuthors;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules.BoostWhenCoupled;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules.BoostWhenLowAverageAge;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules.BoostWhenMoreOutboundLinks;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules.BoostWhenTextualSimilar;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules.ChangePropagationFunction;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules.IgnoreIncomingLinks;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules.BoostWhenEqualComponent;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules.BoostWhenEqualDecisionGroup;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules.StopAtSameElementType;

/**
 * Gathers propagation rules for rule-based change impact analysis.
 * 
 * The implementing classes of {@link ChangePropagationFunction} encode the
 * rules.
 */
public enum ChangePropagationRule {

	STOP_AT_SAME_ELEMENT_TYPE("Stop at elements with the same type as the selected element",
			new StopAtSameElementType()),
	IGNORE_INCOMING_LINKS("Outward links only", new IgnoreIncomingLinks()),
	BOOST_WHEN_TEXTUAL_SIMILAR("Boost when element is textual similar to the selected element",
			new BoostWhenTextualSimilar()),
	BOOST_WHEN_HIGH_AMOUNT_OF_DISTINCT_AUTHORS("Boost when element has a large number of distinct update authors",
			new BoostWhenHighAmountOfDistinctAuthors()),
	BOOST_WHEN_EQUAL_COMPONENT("Boost when element is assigned the same component",
			new BoostWhenEqualComponent()),
	BOOST_WHEN_EQUAL_DECISION_GROUP("Boost when element is assigned the same decision group",
			new BoostWhenEqualDecisionGroup()),
	BOOST_WHEN_MORE_OUTBOUND_THAN_INBOUND("Boost when element has more outbound than inbound links",
			new BoostWhenMoreOutboundLinks()),
	BOOST_WHEN_LOW_AVERAGE_AGE("Boost when element has a low average age",
			new BoostWhenLowAverageAge()),
	BOOST_WHEN_COUPLED("Boost when element is coupled to the selected element",
			new BoostWhenCoupled());

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
	 * @return method with a double return value between 0 and 1 that encodes the
	 *         propagation rule.
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
