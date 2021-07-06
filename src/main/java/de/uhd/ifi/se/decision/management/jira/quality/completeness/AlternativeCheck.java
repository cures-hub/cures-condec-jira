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
		DefinitionOfDone definitionOfDone = ConfigPersistenceManager.getDefinitionOfDone(projectKey);
		return isCompleteAccordingToDefault() && isCompleteAccordingToSettings(definitionOfDone);
	}

	@Override
	public boolean isCompleteAccordingToDefault() {
		return alternative.hasNeighborOfType(KnowledgeType.ISSUE);
	}

	@Override
	public boolean isCompleteAccordingToSettings(DefinitionOfDone definitionOfDone) {
		boolean hasToBeLinkedToArgument = definitionOfDone.isAlternativeIsLinkedToArgument();
		if (hasToBeLinkedToArgument) {
			return alternative.hasNeighborOfType(KnowledgeType.ARGUMENT)
					|| alternative.hasNeighborOfType(KnowledgeType.PRO)
					|| alternative.hasNeighborOfType(KnowledgeType.CON);
		}
		return true;
	}

	@Override
	public List<QualityProblem> getQualityProblems(KnowledgeElement alternative, DefinitionOfDone definitionOfDone) {
		List<QualityProblem> qualityProblems = new ArrayList<>();

		if (!alternative.hasNeighborOfType(KnowledgeType.ISSUE)) {
			qualityProblems.add(QualityProblem.ALTERNATIVEDOESNTHAVEISSUE);
		}

		boolean hasToBeLinkedToArgument = definitionOfDone.isAlternativeIsLinkedToArgument();
		if (hasToBeLinkedToArgument && !(alternative.hasNeighborOfType(KnowledgeType.ARGUMENT)
			|| alternative.hasNeighborOfType(KnowledgeType.PRO)
			|| alternative.hasNeighborOfType(KnowledgeType.CON))) {
			qualityProblems.add(QualityProblem.ALTERNATIVEDOESNTHAVEARGUMENT);
		}

		return qualityProblems;
	}

}
