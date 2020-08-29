package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;

public class DecisionCompletenessCheck implements CompletenessCheck {

	private KnowledgeElement decision;
	private String projectKey;

	@Override
	public boolean execute(KnowledgeElement decision) {
		this.decision = decision;
		projectKey = decision.getProject().getProjectKey();
		return isCompleteAccordingToDefault() && isCompleteAccordingToSettings();
	}

	@Override
	public boolean isCompleteAccordingToDefault() {
		return decision.hasNeighborOfType(KnowledgeType.ISSUE);
	}

	@Override
	public boolean isCompleteAccordingToSettings() {
		boolean hasToBeLinkedToProArgument = ConfigPersistenceManager.getDefinitionOfDone(projectKey)
				.isDecisionIsLinkedToPro();
		if (hasToBeLinkedToProArgument) {
			return decision.hasNeighborOfType(KnowledgeType.PRO);
		}
		return true;
	}

}
