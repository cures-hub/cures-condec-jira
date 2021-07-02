package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;

import java.util.ArrayList;
import java.util.List;

public class AlternativeCheck implements KnowledgeElementCheck<KnowledgeElement> {

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
		return alternative.hasNeighborOfType(KnowledgeType.ISSUE);
	}

	@Override
	public boolean isCompleteAccordingToSettings() {
		boolean hasToBeLinkedToArgument = ConfigPersistenceManager.getDefinitionOfDone(projectKey)
				.isAlternativeIsLinkedToArgument();
		if (hasToBeLinkedToArgument) {
			return alternative.hasNeighborOfType(KnowledgeType.ARGUMENT)
					|| alternative.hasNeighborOfType(KnowledgeType.PRO)
					|| alternative.hasNeighborOfType(KnowledgeType.CON);
		}
		return true;
	}

	@Override
	public List<QualityProblem> getFailedCriteria(KnowledgeElement alternative) {
		List<QualityProblem> failedCriteria = new ArrayList<>();

		if (!alternative.hasNeighborOfType(KnowledgeType.ISSUE)) {
			failedCriteria.add(QualityProblem.ALTERNATIVEDOESNTHAVEISSUE);
		}

		boolean hasToBeLinkedToArgument = ConfigPersistenceManager.getDefinitionOfDone(projectKey)
			.isAlternativeIsLinkedToArgument();
		if (hasToBeLinkedToArgument && !(alternative.hasNeighborOfType(KnowledgeType.ARGUMENT)
			|| alternative.hasNeighborOfType(KnowledgeType.PRO)
			|| alternative.hasNeighborOfType(KnowledgeType.CON))) {
			failedCriteria.add(QualityProblem.ALTERNATIVEDOESNTHAVEARGUMENT);
		}

		return failedCriteria;
	}

}
