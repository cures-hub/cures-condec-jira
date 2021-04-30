package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import org.codehaus.jackson.annotate.JsonProperty;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.Link;

/**
 * Sets rules (criteria) that the decision knowledge documentation needs to
 * fulfill to be complete. These rules can be configured by the rationale
 * manager.
 * 
 * Next to the configurable rules, there are default rules that cannot be
 * configured. For example, a default rule is that each decision problem
 * (=issue) needs to be linked to a decision to be complete.
 * 
 * @see KnowledgeGraph
 * @see KnowledgeElement
 * @see Link
 */
public class DefinitionOfDone {

	private boolean issueIsLinkedToAlternative;
	private boolean decisionIsLinkedToPro;
	private boolean alternativeIsLinkedToArgument;
	private int lineNumbersInCodeFile;
	private int linkDistanceFromCodeFileToDecision;
	private int threshold;

	public DefinitionOfDone() {
		this.issueIsLinkedToAlternative = false;
		this.decisionIsLinkedToPro = false;
		this.alternativeIsLinkedToArgument = false;
		this.linkDistanceFromCodeFileToDecision = 4;
		this.lineNumbersInCodeFile = 50;
		this.threshold = 2;
	}

	/**
	 * @return true if every decision problem (=issue) needs to be linked to at
	 *         least one alternative.
	 */
	public boolean isIssueIsLinkedToAlternative() {
		return issueIsLinkedToAlternative;
	}

	/**
	 * @param issueIsLinkedToAlternative
	 *            true if every decision problem (=issue) needs to be linked to at
	 *            least one alternative.
	 */
	@JsonProperty("issueIsLinkedToAlternative")
	public void setIssueLinkedToAlternative(boolean issueIsLinkedToAlternative) {
		this.issueIsLinkedToAlternative = issueIsLinkedToAlternative;
	}

	/**
	 * @return true if every decision (=solution for a decision problem) needs to be
	 *         linked to at least one pro-argument.
	 */
	public boolean isDecisionIsLinkedToPro() {
		return decisionIsLinkedToPro;
	}

	/**
	 * @param decisionIsLinkedToPro
	 *            true if every decision (=solution for a decision problem) needs to
	 *            be linked to at least one pro-argument.
	 */
	@JsonProperty("decisionIsLinkedToPro")
	public void setDecisionLinkedToPro(boolean decisionIsLinkedToPro) {
		this.decisionIsLinkedToPro = decisionIsLinkedToPro;
	}

	/**
	 * @return true if every alternative (=solution option for a decision problem)
	 *         needs to be linked to at least one pro-argument.
	 */
	public boolean isAlternativeIsLinkedToArgument() {
		return alternativeIsLinkedToArgument;
	}

	/**
	 * @param alternativeIsLinkedToArgument
	 *            true if every alternative (=solution option for a decision
	 *            problem) needs to be linked to at least one pro-argument.
	 */
	@JsonProperty("alternativeIsLinkedToArgument")
	public void setAlternativeLinkedToArgument(boolean alternativeIsLinkedToArgument) {
		this.alternativeIsLinkedToArgument = alternativeIsLinkedToArgument;
	}

	public int getLinkDistanceFromCodeFileToDecision() {
		return linkDistanceFromCodeFileToDecision;
	}

	@JsonProperty("linkDistanceFromCodeFileToDecision")
	public void setLinkDistanceFromCodeFileToDecision(int linkDistanceFromCodeFileToDecision) {
		this.linkDistanceFromCodeFileToDecision = linkDistanceFromCodeFileToDecision;
	}

	public int getLineNumbersInCodeFile() {
		return lineNumbersInCodeFile;
	}

	@JsonProperty("lineNumbersInCodeFile")
	public void setLineNumbersInCodeFile(int lineNumbersInCodeFile) {
		this.lineNumbersInCodeFile = lineNumbersInCodeFile;
	}

	public int getThreshold() {
		return threshold;
	}

	@JsonProperty("threshold")
	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}
}