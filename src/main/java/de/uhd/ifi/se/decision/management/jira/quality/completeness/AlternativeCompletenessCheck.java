package de.uhd.ifi.se.decision.management.jira.quality.completeness;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class AlternativeCompletenessCheck implements CompletenessCheck {

	private KnowledgeElement alternative;
	private String projectKey;

	@Override
	public boolean execute(KnowledgeElement alternative) {
		this.alternative = alternative;
		projectKey = alternative.getProject().getProjectKey();
		return isCompleteAccordingToDefault() && isCompleteAccordingToSettings();
	}

	@Override
	public boolean isCompleteAccordingToDefault() {
		return alternative.hasNeighbourOfType(KnowledgeType.ISSUE);
	}

	@Override
	public boolean isCompleteAccordingToSettings() {
		boolean hasToBeLinkedToArgument =
			ConfigPersistenceManager.getDefinitionOfDone(projectKey).isAlternativeIsLinkedToArgument();
		if (hasToBeLinkedToArgument) {
			return alternative.hasNeighbourOfType(KnowledgeType.ARGUMENT);
		} else return true;
	}
}
