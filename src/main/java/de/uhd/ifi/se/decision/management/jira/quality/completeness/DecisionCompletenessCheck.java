package de.uhd.ifi.se.decision.management.jira.quality.completeness;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class DecisionCompletenessCheck implements CompletenessCheck {

	KnowledgeElement decision;
	String projectKey;

	@Override
	public boolean execute(KnowledgeElement decision) {
		this.decision = decision;
		projectKey = decision.getProject().getProjectKey();
		return isCompleteAccordingToDefault() && isCompleteAccordingToSettings();
	}

	@Override
	public boolean isCompleteAccordingToDefault() {
		return decision.hasNeighbourOfType(KnowledgeType.ISSUE);
	}

	@Override
	public boolean isCompleteAccordingToSettings() {
		boolean hasToBeLinkedToArgument =
			ConfigPersistenceManager.getDefinitionOfDone(projectKey).isDecisionIsLinkedToPro();
		if (hasToBeLinkedToArgument) {
			return decision.hasNeighbourOfType(KnowledgeType.PRO);
		}
		else return true;
	}

}
