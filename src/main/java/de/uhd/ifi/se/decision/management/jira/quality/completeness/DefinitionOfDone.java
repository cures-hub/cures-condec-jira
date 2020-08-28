package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import org.codehaus.jackson.annotate.JsonProperty;

public class DefinitionOfDone {

	private boolean issueIsLinkedToAlternative;
	private boolean decisionIsLinkedToPro;
	private boolean alternativeIsLinkedToArgument;


	public boolean isIssueIsLinkedToAlternative() {
		return issueIsLinkedToAlternative;
	}

	@JsonProperty("issueIsLinkedToAlternative")
	public void setIssueLinkedToAlternative(boolean issueIsLinkedToAlternative) {
		this.issueIsLinkedToAlternative = issueIsLinkedToAlternative;
	}

	public boolean isDecisionIsLinkedToPro() {
		return decisionIsLinkedToPro;
	}

	@JsonProperty("decisionIsLinkedToPro")
	public void setDecisionLinkedToPro(boolean decisionIsLinkedToPro) {
		this.decisionIsLinkedToPro = decisionIsLinkedToPro;
	}


	public boolean isAlternativeIsLinkedToArgument() {
		return alternativeIsLinkedToArgument;
	}

	@JsonProperty("alternativeIsLinkedToArgument")
	public void setAlternativeLinkedToArgument(boolean alternativeIsLinkedToArgument) {
		this.alternativeIsLinkedToArgument = alternativeIsLinkedToArgument;
	}
}
