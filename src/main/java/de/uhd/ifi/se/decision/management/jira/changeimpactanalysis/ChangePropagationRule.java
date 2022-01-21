package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis;

import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules.BoostWhenHighAmountOfDistinctAuthors;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules.BoostWhenTimelyCoupled;
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

	STOP_AT_SAME_ELEMENT_TYPE(
		"Stop at elements with the same type as the selected element",
		"Rule that defines that a change impact is not propagated after a element with the same " +
		"knowledge type was reached. For example, if a change is made in an epic, the change is " +
		"not propagated beyond other epics",
		new StopAtSameElementType()),
	IGNORE_INCOMING_LINKS(
		"Outward links only",
		"Rule that defines that a change impact is not propagated along an incoming link to an " +
		"element. With this rule activated, impacts are only propagated along outgoing links from " +
		"an element.",
		new IgnoreIncomingLinks()),
	BOOST_WHEN_TEXTUAL_SIMILAR(
		"Boost when element is textual similar to the selected element",
		"Rule that defines that a change impact is stronger propagated if the traversed element " +
		"in the knowledge graph is textual similar to the selected element.",
		new BoostWhenTextualSimilar()),
	BOOST_WHEN_HIGH_AMOUNT_OF_DISTINCT_AUTHORS(
		"Boost when element has a large number of distinct update authors",
		"Rule that defines that a change impact is stronger propagated if the traversed element " +
		"in the knowledge graph has a large amount of distinct update authors.",
		new BoostWhenHighAmountOfDistinctAuthors()),
	BOOST_WHEN_EQUAL_COMPONENT(
		"Boost when element is assigned the same component",
		"Rule that defines that a change impact is stronger propagated if the traversed element " +
		"in the knowledge graph is assigned to the same component.",
		new BoostWhenEqualComponent()),
	BOOST_WHEN_EQUAL_DECISION_GROUP(
		"Boost when element is assigned the same decision group",
		"Rule that defines that a change impact is stronger propagated if the traversed element " +
		"in the knowledge graph is assigned to the same decision group.",
		new BoostWhenEqualDecisionGroup()),
	BOOST_WHEN_MORE_OUTBOUND_THAN_INBOUND(
		"Boost when element has more outbound than inbound links",
		"Rule that defines that a change impact is stronger propagated if the traversed element " +
		"in the knowledge graph has more outbound links than inbound links.",
		new BoostWhenMoreOutboundLinks()),
	BOOST_WHEN_LOW_AVERAGE_AGE(
		"Boost when element has a low average age",
		"Rule that defines that a change impact is stronger propagated if the traversed element " +
		"in the knowledge graph has a low average age. The average age is determined by taking deducing " +
		"the difference between the creation date and the latest update date. A high difference " +
		"indicates a high average age.",
		new BoostWhenLowAverageAge()),
	BOOST_WHEN_TIMELY_COUPLED(
		"Boost when element is timely coupled to the selected element",
		"Rule that defines that a change impact is stronger propagated if the traversed element " +
		"in the knowledge graph is timely coupled with the source element. Elements are assumed " +
		"to be timely coupled if they have received updates in the same timeframe, i.e. within 10 minutes.",
		new BoostWhenTimelyCoupled());

	private String description;
	private ChangePropagationFunction function;
	private String explanation;
	private boolean isActive;
	private float weightValue;

	private ChangePropagationRule(String description, String explanation, ChangePropagationFunction predicate) {
		this.description = description;
		this.explanation = explanation;
		this.function = predicate;
		this.weightValue = 1.0f;
		this.isActive = false;
	}

	/**
	 * @return description of the change propagation rule.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return in-depth explanation of the change propagation rule.
	 */
	public String getExplanation() {
		return explanation;
	}

	/**
	 * @return the activation status of the change propagation rule.
	 */
	public boolean isActive() {
		return isActive;
	}

	/**
	 * @return the weight value of the change propagation rule.
	 */
	public float getWeightValue() {
		return weightValue;
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
