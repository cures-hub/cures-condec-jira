package de.uhd.ifi.se.decision.management.jira.quality;

import java.util.ArrayList;
import java.util.List;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class AlternativeCheck extends KnowledgeElementCheck {

	public AlternativeCheck(KnowledgeElement elementToBeChecked) {
		super(elementToBeChecked);
	}

	@Override
	public List<QualityCriterionCheckResult> getQualityCheckResult(DefinitionOfDone definitionOfDone) {

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
		return !element.getLinkedDecisionProblems().isEmpty();
	}

	private boolean hasArgument() {
		return element.hasNeighborOfType(KnowledgeType.ARGUMENT) || element.hasNeighborOfType(KnowledgeType.PRO)
				|| element.hasNeighborOfType(KnowledgeType.CON);
	}

}
