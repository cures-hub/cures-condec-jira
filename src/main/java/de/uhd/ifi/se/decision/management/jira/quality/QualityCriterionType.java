package de.uhd.ifi.se.decision.management.jira.quality;

/**
 * Models the quality criteria of knowledge elements. Quality criteria are
 * checked for issues (=decision problems), decisions, alternatives, arguments,
 * and code files.
 *
 * Used in {@link KnowledgeElementCheck}.
 */
public enum QualityCriterionType {
	NO_DECISION_COVERAGE("There are no decisions documented.", ""), //
	DECISION_COVERAGE_TOO_LOW("Minimum decision coverage is not reached.", "Decision coverage is ok."), //
	INCOMPLETE_KNOWLEDGE_LINKED("Linked decision knowledge is incomplete.", "Linked decision knowledge is fine."), //
	ISSUE_DOESNT_HAVE_DECISION("Issue does not have a valid decision!", "Issue is solved by decision."), //
	ISSUE_DOESNT_HAVE_ALTERNATIVE("Issue does not have an alternative!",
			"At least one alternative is documented for issue."), //
	ISSUE_IS_UNRESOLVED("Issue is unresolved!", "Issue is resolved."), //
	DECISION_DOESNT_HAVE_ISSUE("Decision does not have an issue!", "Decision is linked to issue."), //
	DECISION_DOESNT_HAVE_PRO("Decision does not have a pro-argument!",
			"At least one pro-argument is documented for decision."), //
	DECISION_IS_CHALLENGED("Decision is challenged, i.e., it needs more discussion and might be rejected!",
			"Decision is decided."), //
	ALTERNATIVE_DOESNT_HAVE_ISSUE("Alternative does not have an issue!", "Alternative is linked to issue."), //
	ALTERNATIVE_DOESNT_HAVE_ARGUMENT("Alternative does not have an argument!",
			"At least one argument is documented for alternative."), //
	ARGUMENT_DOESNT_HAVE_DECISION_OR_ALTERNATIVE("Argument does not have a decision or an alternative!",
			"Argument is linked to solution option.");

	private String violationDescription;
	private String fulfillmentDescription;

	private QualityCriterionType(String violationDescription, String fulfillmentDescription) {
		this.violationDescription = violationDescription;
		this.fulfillmentDescription = fulfillmentDescription;
	}

	public String getViolationDescription() {
		return violationDescription;
	}

	public String getFulfillmentDescription() {
		return fulfillmentDescription;
	}
}