package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import java.util.Locale;

/**
 * Models the possible quality problems of knowledge elements. Quality problems
 * are checked for issues, decisions, alternatives, arguments and code files.
 *
 * Used in {@link KnowledgeElementCheck}.
 */
public enum QualityProblem {
	DECISIONCOVERAGETOOLOW("Minimum decision coverage is not reached."),
	INCOMPLETEKNOWLEDGELINKED("Linked decision knowledge is incomplete."),
	ISSUEDOESNTHAVEDECISION("Issue doesn't have a valid decision!"),
	ISSUEDOESNTHAVEALTERNATIVE("Issue doesn't have an alternative!"),
	ISSUEISUNRESOLVED("Issue is unresolved!"),
	DECISIONDOESNTHAVEISSUE("Decision doesn't have an issue!"),
	DECISIONDOESNTHAVEPRO("Decision doesn't have a pro-argument!"),
	ALTERNATIVEDOESNTHAVEISSUE("Alternative doesn't have an issue!"),
	ALTERNATIVEDOESNTHAVEARGUMENT("Alternative doesn't have an argument!"),
	ARGUMENTDOESNTHAVEDECISIONORALTERNATIVE("Argument doesn't have a decision or an alternative!");

	private String description;

	private QualityProblem(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

}
