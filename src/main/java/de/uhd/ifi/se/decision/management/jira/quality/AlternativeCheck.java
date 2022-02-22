package de.uhd.ifi.se.decision.management.jira.quality;

import java.util.ArrayList;
import java.util.List;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

/**
 * Checks whether an alternative fulfills the {@link DefinitionOfDone}.
 */
public class AlternativeCheck extends KnowledgeElementCheck {

	public AlternativeCheck(KnowledgeElement elementToBeChecked) {
		super(elementToBeChecked);
	}

	@Override
	public List<QualityCriterionCheckResult> getQualityCheckResult(DefinitionOfDone definitionOfDone) {
		List<QualityCriterionCheckResult> qualityCheckResults = new ArrayList<>();
		qualityCheckResults.add(checkAlternativeLinkedToIssue(element));
		if (definitionOfDone.isAlternativeIsLinkedToArgument()) {
			qualityCheckResults.add(checkAlternativeLinkedToArgument(element));
		}
		qualityCheckResults.add(DecisionCheck.checkDecisionLevelAssigned(element));
		return qualityCheckResults;
	}

	private QualityCriterionCheckResult checkAlternativeLinkedToIssue(KnowledgeElement alternative) {
		if (alternative.getLinkedDecisionProblems().isEmpty()) {
			return new QualityCriterionCheckResult(QualityCriterionType.ALTERNATIVE_LINKED_TO_ISSUE, true);
		}
		return new QualityCriterionCheckResult(QualityCriterionType.ALTERNATIVE_LINKED_TO_ISSUE, false);
	}

	private QualityCriterionCheckResult checkAlternativeLinkedToArgument(KnowledgeElement alternative) {
		if (hasArgument(alternative)) {
			return new QualityCriterionCheckResult(QualityCriterionType.ALTERNATIVE_LINKED_TO_ARGUMENT, false);
		}
		return new QualityCriterionCheckResult(QualityCriterionType.ALTERNATIVE_LINKED_TO_ARGUMENT, true);
	}

	private boolean hasArgument(KnowledgeElement alternative) {
		return alternative.hasNeighborOfType(KnowledgeType.ARGUMENT) || alternative.hasNeighborOfType(KnowledgeType.PRO)
				|| alternative.hasNeighborOfType(KnowledgeType.CON);
	}

}
