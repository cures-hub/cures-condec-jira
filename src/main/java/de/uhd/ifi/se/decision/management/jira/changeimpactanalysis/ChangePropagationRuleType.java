package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis;

import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules.BoostIfDecisionProblem;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules.BoostIfSolutionOption;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules.BoostWhenEqualComponent;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules.BoostWhenEqualDecisionGroup;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules.BoostWhenEqualKnowledgeType;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules.BoostWhenHighAmountOfDistinctAuthors;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules.BoostWhenLowAverageAge;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules.BoostWhenMoreOutboundLinks;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules.BoostWhenTextualSimilar;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules.BoostWhenTimelyCoupled;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules.ChangePropagationFunction;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules.IgnoreIncomingLinks;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules.StopAtSameElementType;

/**
 * Gathers propagation rules for rule-based change impact analysis.
 * 
 * The implementing classes of {@link ChangePropagationFunction} encode the
 * rules.
 */
public enum ChangePropagationRuleType {

	STOP_AT_SAME_ELEMENT_TYPE("Stop at elements with the same type as the selected element",
			"Rule that defines that a change impact is not propagated after a element with the same "
					+ "knowledge type was reached. For example, if a change is made in an epic, the change is "
					+ "not propagated beyond other epics",
			new StopAtSameElementType()), //
	IGNORE_INCOMING_LINKS("Outward links only",
			"Rule that defines that a change impact is not propagated along an incoming link to an "
					+ "element. With this rule activated, impacts are only propagated along outgoing links from "
					+ "an element.",
			new IgnoreIncomingLinks()), //
	BOOST_WHEN_TEXTUAL_SIMILAR("Boost when element is textual similar to the selected element",
			"Rule that defines that a change impact is stronger propagated if the traversed element "
					+ "in the knowledge graph is textual similar to the selected element.",
			new BoostWhenTextualSimilar()), //
	BOOST_WHEN_HIGH_AMOUNT_OF_DISTINCT_AUTHORS("Boost when element has a large number of distinct update authors",
			"Rule that defines that a change impact is stronger propagated if the traversed element "
					+ "in the knowledge graph has a large number of distinct update authors.",
			new BoostWhenHighAmountOfDistinctAuthors()), //
	BOOST_WHEN_EQUAL_COMPONENT("Boost when element is assigned the same component",
			"Rule that defines that a change impact is stronger propagated if the traversed element "
					+ "in the knowledge graph is assigned to the same component.",
			new BoostWhenEqualComponent()), //
	BOOST_WHEN_EQUAL_DECISION_GROUP("Boost when element is assigned the same decision group",
			"Rule that defines that a change impact is stronger propagated if the traversed element "
					+ "in the knowledge graph is assigned to the same decision group.",
			new BoostWhenEqualDecisionGroup()), //
	BOOST_WHEN_MORE_OUTBOUND_THAN_INBOUND("Boost when element has more outbound than inbound links",
			"Rule that defines that a change impact is stronger propagated if the traversed element "
					+ "in the knowledge graph has more outbound links than inbound links.",
			new BoostWhenMoreOutboundLinks()), //
	BOOST_WHEN_EQUAL_KNOWLEDGE_TYPE("Boost when element has the same knowledge type",
			"Rule that defines that a change impact is stronger propagated if the traversed element "
					+ "in the knowledge graph has the same knowledge type, e.g. decision, argument, code.",
			new BoostWhenEqualKnowledgeType()), //
	BOOST_IF_DECISION_PROBLEM("Boost when element is a decision problem",
			"Rule that defines that a change impact is stronger propagated if the traversed element "
					+ "in the knowledge graph is a decision problem.",
			new BoostIfDecisionProblem()), //
	BOOST_IF_SOLUTION_OPTION("Boost when element is a solution option",
			"Rule that defines that a change impact is stronger propagated if the traversed element "
					+ "in the knowledge graph is a solution option.",
			new BoostIfSolutionOption()), //
	BOOST_WHEN_LOW_AVERAGE_AGE("Boost when element has a low average age",
			"Rule that defines that a change impact is stronger propagated if the traversed element "
					+ "in the knowledge graph has a low average age. The average age is determined by deducing "
					+ "the creation date from the latest update date. A high difference "
					+ "indicates a high average age.",
			new BoostWhenLowAverageAge()), //
	BOOST_WHEN_TIMELY_COUPLED("Boost when element is timely coupled to the selected element",
			"Rule that defines that a change impact is stronger propagated if the traversed element "
					+ "in the knowledge graph is timely coupled to the source element. Elements are assumed "
					+ "to be timely coupled if they have received updates in the same timeframe, i.e. within 10 minutes.",
			new BoostWhenTimelyCoupled());

	private String description;
	private ChangePropagationFunction function;
	private String explanation;

	private ChangePropagationRuleType(String description, String explanation, ChangePropagationFunction predicate) {
		this.description = description;
		this.explanation = explanation;
		this.function = predicate;
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
	 * @return method with a double return value between 0 and 1 that encodes the
	 *         propagation rule.
	 * @see ChangePropagationFunction#isChangePropagated
	 */
	public ChangePropagationFunction getFunction() {
		return function;
	}

	/**
	 * @param ruleType
	 * 			description of a rule type.
	 * @return {@link ChangePropagationRuleType} which matches the given description.
	 */
	public static ChangePropagationRuleType fromString(String ruleDescription) {
        for (ChangePropagationRuleType type : ChangePropagationRuleType.values()) {
            if (type.description.equalsIgnoreCase(ruleDescription)) {
                return type;
            }
        }
        return null;
    }
}
