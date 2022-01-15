package de.uhd.ifi.se.decision.management.jira.quality;

import java.util.ArrayList;
import java.util.List;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;

public class AlternativeCheck extends KnowledgeElementCheck {

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
		return !definitionOfDone.isAlternativeIsLinkedToArgument() || hasArgument();
	}

	@Override
	public List<QualityCriterionCheckResult> getQualityCheckResult(KnowledgeElement alternative,
			DefinitionOfDone definitionOfDone) {
		this.alternative = alternative;

		List<QualityCriterionCheckResult> qualityCheckResults = new ArrayList<>();

		if (!hasDecisionProblem()) {
			qualityCheckResults
					.add(new QualityCriterionCheckResult(QualityCriterionType.ALTERNATIVE_LINKED_TO_ISSUE, true));
		} else {
			qualityCheckResults
					.add(new QualityCriterionCheckResult(QualityCriterionType.ALTERNATIVE_LINKED_TO_ISSUE, false));
		}

		if (definitionOfDone.isAlternativeIsLinkedToArgument()) {
			if (!hasArgument()) {
				qualityCheckResults.add(
						new QualityCriterionCheckResult(QualityCriterionType.ALTERNATIVE_LINKED_TO_ARGUMENT, true));
			} else {
				qualityCheckResults.add(
						new QualityCriterionCheckResult(QualityCriterionType.ALTERNATIVE_LINKED_TO_ARGUMENT, false));
			}
		}

		return qualityCheckResults;
	}

	private boolean hasDecisionProblem() {
		return !alternative.getLinkedDecisionProblems().isEmpty();
	}

	private boolean hasArgument() {
		return alternative.hasNeighborOfType(KnowledgeType.ARGUMENT) || alternative.hasNeighborOfType(KnowledgeType.PRO)
				|| alternative.hasNeighborOfType(KnowledgeType.CON);
	}

}
