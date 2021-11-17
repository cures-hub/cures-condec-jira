package de.uhd.ifi.se.decision.management.jira.quality.completeness;

/**
 * Models the possible quality problems of knowledge elements. Quality problems
 * are checked for issues (=decision problems), decisions, alternatives,
 * arguments, and code files.
 *
 * Used in {@link KnowledgeElementCheck}.
 */
public enum QualityProblemType {
	NO_DECISION_COVERAGE("There are no decisions documented."), //
	DECISION_COVERAGE_TOO_LOW("Minimum decision coverage is not reached."), //
	INCOMPLETE_KNOWLEDGE_LINKED("Linked decision knowledge is incomplete."), //
	ISSUE_DOESNT_HAVE_DECISION("Issue doesn't have a valid decision!"), //
	ISSUE_DOESNT_HAVE_ALTERNATIVE("Issue doesn't have an alternative!"), //
	ISSUE_IS_UNRESOLVED("Issue is unresolved!"), //
	DECISION_DOESNT_HAVE_ISSUE("Decision doesn't have an issue!"), //
	DECISION_DOESNT_HAVE_PRO("Decision doesn't have a pro-argument!"), //
	DECISION_IS_CHALLENGED("Decision is challenged, i.e., it needs more discussion and might be rejected!"), //
	ALTERNATIVE_DOESNT_HAVE_ISSUE("Alternative doesn't have an issue!"), //
	ALTERNATIVE_DOESNT_HAVE_ARGUMENT("Alternative doesn't have an argument!"), //
	ARGUMENT_DOESNT_HAVE_DECISION_OR_ALTERNATIVE("Argument doesn't have a decision or an alternative!");

	private String description;

	private QualityProblemType(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}