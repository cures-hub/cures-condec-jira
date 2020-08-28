package de.uhd.ifi.se.decision.management.jira.quality.completeness;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class ArgumentCompletenessCheck implements CompletenessCheck {

	private KnowledgeElement argument;
	private String projectKey;

	@Override
	public boolean execute(KnowledgeElement argument) {
		this.argument = argument;
		projectKey = argument.getProject().getProjectKey();
		return isCompleteAccordingToDefault() && isCompleteAccordingToSettings();
	}

	@Override
	public boolean isCompleteAccordingToDefault() {
		return argument.hasNeighbourOfType(KnowledgeType.DECISION) || argument.hasNeighbourOfType(KnowledgeType.ALTERNATIVE);
	}

	@Override
	public boolean isCompleteAccordingToSettings() {
		return true;
	}

}
