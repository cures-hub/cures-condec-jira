package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import org.codehaus.jackson.annotate.JsonProperty;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.Link;

import java.util.HashMap;
import java.util.Map;

/**
 * Sets rules that the decision knowledge documentation needs to fulfill to be
 * complete. These rules can be configured by the rationale manager.
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

	private Map<String, Boolean> criteriaMap = new HashMap<>();

	public DefinitionOfDone() {
		criteriaMap.put("issueIsLinkedToAlternative", Boolean.FALSE);
		criteriaMap.put("decisionIsLinkedToPro", Boolean.FALSE);
		criteriaMap.put("alternativeIsLinkedToArgument", Boolean.FALSE);
	}

	public Map<String, Boolean> getCriteriaMap() {
		return criteriaMap;
	}

	/**
	 * @return true if every decision problem (=issue) needs to be linked to at
	 *         least one alternative.
	 */
	public boolean isIssueIsLinkedToAlternative() {
		return (boolean) criteriaMap.get("issueIsLinkedToAlternative");
	}

	/**
	 * @param issueIsLinkedToAlternative
	 *            true if every decision problem (=issue) needs to be linked to at
	 *            least one alternative.
	 */
	@JsonProperty("issueIsLinkedToAlternative")
	public void setIssueLinkedToAlternative(boolean issueIsLinkedToAlternative) {
		criteriaMap.put("issueIsLinkedToAlternative", issueIsLinkedToAlternative);
	}

	/**
	 * @return true if every decision (=solution for a decision problem) needs to be
	 *         linked to at least one pro-argument.
	 */
	public boolean isDecisionIsLinkedToPro() {
		return criteriaMap.get("decisionIsLinkedToPro");
	}

	/**
	 * @param decisionIsLinkedToPro
	 *            true if every decision (=solution for a decision problem) needs to
	 *            be linked to at least one pro-argument.
	 */
	@JsonProperty("decisionIsLinkedToPro")
	public void setDecisionLinkedToPro(boolean decisionIsLinkedToPro) {
		criteriaMap.put("decisionIsLinkedToPro", decisionIsLinkedToPro);
	}

	/**
	 * @return true if every alternative (=solution option for a decision problem)
	 *         needs to be linked to at least one pro-argument.
	 */
	public boolean isAlternativeIsLinkedToArgument() {
		return criteriaMap.get("alternativeIsLinkedToArgument");
	}

	/**
	 * @param alternativeIsLinkedToArgument
	 *            true if every alternative (=solution option for a decision
	 *            problem) needs to be linked to at least one pro-argument.
	 */
	@JsonProperty("alternativeIsLinkedToArgument")
	public void setAlternativeLinkedToArgument(boolean alternativeIsLinkedToArgument) {
		criteriaMap.put("alternativeIsLinkedToArgument", alternativeIsLinkedToArgument);
	}
}
