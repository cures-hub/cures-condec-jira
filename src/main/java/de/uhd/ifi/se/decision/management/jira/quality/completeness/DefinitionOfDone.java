package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonProperty;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.Link;

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

	private Map<String, Integer> criteriaMap = new HashMap<>();

	public DefinitionOfDone() {
		criteriaMap.put("issueIsLinkedToAlternative", 0);
		criteriaMap.put("decisionIsLinkedToPro", 0);
		criteriaMap.put("alternativeIsLinkedToArgument", 0);
		criteriaMap.put("linkDistanceFromCodeFileToDecision", 4);
		criteriaMap.put("lineNumbersInCodeFile", 50);
		criteriaMap.put("threshold", 2);
	}

	public Map<String, Integer> getCriteriaMap() {
		return criteriaMap;
	}

	/**
	 * @return true if every decision problem (=issue) needs to be linked to at
	 * least one alternative.
	 */
	public boolean isIssueIsLinkedToAlternative() {
		return criteriaMap.get("issueIsLinkedToAlternative") != 0;
	}

	/**
	 * @param issueIsLinkedToAlternative true if every decision problem (=issue) needs to be linked to at
	 *                                   least one alternative.
	 */
	@JsonProperty("issueIsLinkedToAlternative")
	public void setIssueLinkedToAlternative(boolean issueIsLinkedToAlternative) {
		if (issueIsLinkedToAlternative) {
			criteriaMap.put("issueIsLinkedToAlternative", 1);
		} else {
			criteriaMap.put("issueIsLinkedToAlternative", 0);
		}
	}

	/**
	 * @return true if every decision (=solution for a decision problem) needs to be
	 * linked to at least one pro-argument.
	 */
	public boolean isDecisionIsLinkedToPro() {
		return criteriaMap.get("decisionIsLinkedToPro") != 0;
	}

	/**
	 * @param decisionIsLinkedToPro true if every decision (=solution for a decision problem) needs to
	 *                              be linked to at least one pro-argument.
	 */
	@JsonProperty("decisionIsLinkedToPro")
	public void setDecisionLinkedToPro(boolean decisionIsLinkedToPro) {
		if (decisionIsLinkedToPro) {
			criteriaMap.put("decisionIsLinkedToPro", 1);
		} else {
			criteriaMap.put("decisionIsLinkedToPro", 0);
		}
	}

	/**
	 * @return true if every alternative (=solution option for a decision problem)
	 * needs to be linked to at least one pro-argument.
	 */
	public boolean isAlternativeIsLinkedToArgument() {
		return criteriaMap.get("alternativeIsLinkedToArgument") != 0;
	}

	/**
	 * @param alternativeIsLinkedToArgument true if every alternative (=solution option for a decision
	 *                                      problem) needs to be linked to at least one pro-argument.
	 */
	@JsonProperty("alternativeIsLinkedToArgument")
	public void setAlternativeLinkedToArgument(boolean alternativeIsLinkedToArgument) {
		if (alternativeIsLinkedToArgument) {
			criteriaMap.put("alternativeIsLinkedToArgument", 1);
		} else {
			criteriaMap.put("alternativeIsLinkedToArgument", 0);
		}
	}

	public int getLinkDistanceFromCodeFileToDecision() {
		if (!criteriaMap.containsKey("linkDistanceFromCodeFileToDecision")) {
			criteriaMap.put("linkDistanceFromCodeFileToDecision", 4);
		}
		return criteriaMap.get("linkDistanceFromCodeFileToDecision");
	}

	@JsonProperty("linkDistanceFromCodeFileToDecision")
	public void setLinkDistanceFromCodeFileToDecision(int linkDistanceFromCodeFileToDecision) {
		criteriaMap.put("linkDistanceFromCodeFileToDecision", linkDistanceFromCodeFileToDecision);
	}

	public int getLineNumbersInCodeFile() {
		if (!criteriaMap.containsKey("lineNumbersInCodeFile")) {
			criteriaMap.put("lineNumbersInCodeFile", 50);
		}
		return criteriaMap.get("lineNumbersInCodeFile");
	}

	@JsonProperty("lineNumbersInCodeFile")
	public void setLineNumbersInCodeFile(int lineNumbersInCodeFile) {
		criteriaMap.put("lineNumbersInCodeFile", lineNumbersInCodeFile);
	}

	public int getThreshold() {
		if (!criteriaMap.containsKey("threshold")) {
			criteriaMap.put("threshold", 2);
		}
		return criteriaMap.get("threshold");
	}

	@JsonProperty("threshold")
	public void setThreshold(int threshold) {
		criteriaMap.put("threshold", threshold);
	}
}