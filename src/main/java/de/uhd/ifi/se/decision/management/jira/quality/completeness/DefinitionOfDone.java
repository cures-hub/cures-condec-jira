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
	private int maximumLinkDistanceToDecisions;
	private int minimumDecisionsWithinLinkDistance;

	public DefinitionOfDone() {
		this.issueIsLinkedToAlternative = false;
		this.decisionIsLinkedToPro = false;
		this.alternativeIsLinkedToArgument = false;
		this.maximumLinkDistanceToDecisions = 4;
		this.lineNumbersInCodeFile = 50;
		this.minimumDecisionsWithinLinkDistance = 2;
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

	public int getLineNumbersInCodeFile() {
		return lineNumbersInCodeFile;
	}

	@JsonProperty("lineNumbersInCodeFile")
	public void setLineNumbersInCodeFile(int lineNumbersInCodeFile) {
		this.lineNumbersInCodeFile = lineNumbersInCodeFile;
	}

	/**
	 * @return maximum link distance starting from a requirement, code files (or
	 *         other software artifact = knowledge element) in that a certain number
	 *         of decisions
	 *         ({@link #getMinimumNumberOfDecisionsWithinLinkDistance()}) needs to
	 *         be documented. For example, in order to fulfill the definition of
	 *         done, a code file needs to have at least two decisions documented
	 *         within a link distance of 2. This defines the minimal
	 *         rationale/decision coverage for the code file.
	 */
	public int getMaximumLinkDistanceToDecisions() {
		return maximumLinkDistanceToDecisions;
	}

	/**
	 * @param maximumLinkDistanceToDecisions
	 *            maximum link distance starting from a requirement, code files (or
	 *            other software artifact = knowledge element) in that a certain
	 *            number of decisions
	 *            ({@link #getMinimumNumberOfDecisionsWithinLinkDistance()}) needs
	 *            to be documented. For example, in order to fulfill the definition
	 *            of done, a code file needs to have at least two decisions
	 *            documented within a link distance of 2. This defines the minimal
	 *            rationale/decision coverage for the code file.
	 */
	@JsonProperty("maximumLinkDistanceToDecisions")
	public void setMaximumLinkDistanceToDecisions(int maximumLinkDistanceToDecisions) {
		this.maximumLinkDistanceToDecisions = maximumLinkDistanceToDecisions;
	}

	/**
	 * @return minimum number of decisions that need to be documented within the
	 *         {@link #getMaximumLinkDistanceToDecisions}. For example, in order to
	 *         fulfill the definition of done, a requirement needs to have at least
	 *         one decision documented within a link distance of 3. This defines the
	 *         minimal rationale/decision coverage for the requirement.
	 */
	public int getMinimumNumberOfDecisionsWithinLinkDistance() {
		return minimumDecisionsWithinLinkDistance;
	}

	/**
	 * @param minimumNumberOfDecisionsWithinLinkDistance
	 *            minimum number of decisions that need to be documented within the
	 *            {@link #getMaximumLinkDistanceToDecisions}. For example, in order
	 *            to fulfill the definition of done, a requirement needs to have at
	 *            least one decision documented within a link distance of 3. This
	 *            defines the minimal rationale/decision coverage for the
	 *            requirement.
	 */
	@JsonProperty("minimumNumberOfDecisions")
	public void setMinimumNumberOfDecisionsWithinLinkDistance(int minimumNumberOfDecisionsWithinLinkDistance) {
		this.minimumDecisionsWithinLinkDistance = minimumNumberOfDecisionsWithinLinkDistance;
	}
}