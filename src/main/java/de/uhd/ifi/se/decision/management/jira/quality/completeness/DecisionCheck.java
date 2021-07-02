package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;

import java.util.ArrayList;
import java.util.List;

public class DecisionCheck implements KnowledgeElementCheck<KnowledgeElement> {

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

	@Override
	public List<QualityProblem> getFailedCriteria(KnowledgeElement decision) {
		List<QualityProblem> failedCriteria = new ArrayList<>();

		if (!decision.hasNeighborOfType(KnowledgeType.ISSUE)) {
			failedCriteria.add(QualityProblem.DECISIONDOESNTHAVEISSUE);
		}

		boolean hasToBeLinkedToProArgument = ConfigPersistenceManager.getDefinitionOfDone(projectKey)
			.isDecisionIsLinkedToPro();
		if (hasToBeLinkedToProArgument && !decision.hasNeighborOfType(KnowledgeType.PRO)) {
			failedCriteria.add(QualityProblem.DECISIONDOESNTHAVEPRO);
		}

		return failedCriteria;
	}

}
