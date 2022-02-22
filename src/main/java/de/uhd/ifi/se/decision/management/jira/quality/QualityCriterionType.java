package de.uhd.ifi.se.decision.management.jira.quality;

import org.apache.commons.lang.StringUtils;

/**
 * Models the quality criteria of knowledge elements. Quality criteria are
 * checked for issues (=decision problems), decisions, alternatives, arguments,
 * and code files.
 *
 * Used in {@link KnowledgeElementCheck}.
 */
public enum QualityCriterionType {
	DECISION_COVERAGE("Minimum decision coverage is not reached.", "Decision coverage is ok."), //
	QUALITY_OF_LINKED_KNOWLEDGE(
			"Linked knowledge violates the DoD! (see knowledge graph views to understand what is violated)",
			"Linked knowledge fulfills the DoD."), //
	ISSUE_LINKED_TO_DECISION("Issue does not have a valid decision!", "Issue is solved by decision."), //
	ISSUE_LINKED_TO_ALTERNATIVE("Issue does not have an alternative!",
			"At least one alternative is documented for the issue."), //
	ISSUE_STATUS("Issue status is unresolved!", "Issue status is resolved."), //
	DECISION_LINKED_TO_ISSUE("Decision does not have an issue!", "Decision is linked to an issue."), //
	DECISION_LINKED_TO_PRO("Decision does not have a pro-argument!",
			"At least one pro-argument is documented for the decision."), //
	DECISION_STATUS("Decision is challenged, i.e., it needs more discussion and might be rejected!",
			"Decision is not challenged."), //
	ALTERNATIVE_LINKED_TO_ISSUE("Alternative does not have an issue!", "Alternative is linked to issue."), //
	ALTERNATIVE_LINKED_TO_ARGUMENT("Alternative does not have an argument!",
			"At least one argument is documented for the alternative."), //
	ARGUMENT_LINKED_TO_SOLUTION_OPTION("Argument does not have a decision or an alternative!",
			"Argument is linked to a solution option."), //
	DECISION_LEVEL_ASSIGNED("Decision level (high, medium, realization) is missing.", "Decision level is choosen.");

	private String violationDescription;
	private String fulfillmentDescription;

	private QualityCriterionType(String violationDescription, String fulfillmentDescription) {
		this.violationDescription = violationDescription;
		this.fulfillmentDescription = fulfillmentDescription;
	}

	/**
	 * @return default text describing the quality criterion if it is violated.
	 */
	public String getViolationDescription() {
		return violationDescription;
	}

	/**
	 * @return default text describing the quality criterion if it is fulfilled.
	 */
	public String getFulfillmentDescription() {
		return fulfillmentDescription;
	}

	public String toString() {
		return StringUtils.capitalize(name().replaceAll("_", " ").toLowerCase());
	}
}