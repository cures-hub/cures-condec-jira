package de.uhd.ifi.se.decision.management.jira.quality;

import java.util.ArrayList;
import java.util.List;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;

public class AlternativeCheck implements KnowledgeElementCheck {

	private KnowledgeElement alternative;

	@Override
	public boolean execute(KnowledgeElement alternative) {
		this.alternative = alternative;
		String projectKey = alternative.getProject().getProjectKey();
		DefinitionOfDone definitionOfDone = ConfigPersistenceManager.getDefinitionOfDone(projectKey);
		return isCompleteAccordingToDefault() && isCompleteAccordingToSettings(definitionOfDone);
	}

	@Override
	public boolean isCompleteAccordingToDefault() {
		return hasDecisionProblem();
	}

	@Override
	public boolean isCompleteAccordingToSettings(DefinitionOfDone definitionOfDone) {
		return hasArgument(definitionOfDone);
	}

	@Override
	public List<QualityCriterionCheckResult> getQualityProblems(KnowledgeElement alternative, DefinitionOfDone definitionOfDone) {
		this.alternative = alternative;

		List<QualityCriterionCheckResult> qualityProblems = new ArrayList<>();

		if (!hasDecisionProblem()) {
			qualityProblems.add(new QualityCriterionCheckResult(QualityCriterionType.ALTERNATIVE_DOESNT_HAVE_ISSUE));
		}

		if (!hasArgument(definitionOfDone)) {
			qualityProblems.add(new QualityCriterionCheckResult(QualityCriterionType.ALTERNATIVE_DOESNT_HAVE_ARGUMENT));
		}

		return qualityProblems;
	}

	private boolean hasDecisionProblem() {
		return !alternative.getLinkedDecisionProblems().isEmpty();
	}

	private boolean hasArgument(DefinitionOfDone definitionOfDone) {
		boolean hasToBeLinkedToArgument = definitionOfDone.isAlternativeIsLinkedToArgument();
		if (hasToBeLinkedToArgument) {
			return alternative.hasNeighborOfType(KnowledgeType.ARGUMENT)
					|| alternative.hasNeighborOfType(KnowledgeType.PRO)
					|| alternative.hasNeighborOfType(KnowledgeType.CON);
		}
		return true;
	}

}
