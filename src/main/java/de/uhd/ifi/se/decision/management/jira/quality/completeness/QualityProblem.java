package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Models the possible quality problems of knowledge elements. Quality problems
 * are checked for issues, decisions, alternatives, arguments and code files.
 *
 * Used in {@link KnowledgeElementCheck}.
 */
public enum QualityProblem {
	NODECISIONCOVERAGE("There are no decisions documented."),
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

	private final String description;

	private QualityProblem(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	/**
	 * Creates a json object out of this object.
	 *
	 * @return an ObjectNode containing the id as key and
	 *         the description as value.
	 */
	public ObjectNode getJson() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode node = mapper.createObjectNode();
		node.put("name", this.name());
		node.put("description", this.getDescription());
		return node;
	}

}
