package de.uhd.ifi.se.decision.management.jira.quality;

import java.util.ArrayList;
import java.util.List;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

/**
 * Checks whether a decision fulfills the {@link DefinitionOfDone}.
 */
public class DecisionCheck extends KnowledgeElementCheck {

	public DecisionCheck(KnowledgeElement elementToBeChecked) {
		super(elementToBeChecked);
	}

	@Override
	public List<QualityCriterionCheckResult> getQualityCheckResult(DefinitionOfDone definitionOfDone) {
		List<QualityCriterionCheckResult> qualityCheckResults = new ArrayList<>();
		qualityCheckResults.add(checkDecisionLinkedToIssue(element));
		qualityCheckResults.add(checkDecisionStatus(element));
		if (definitionOfDone.isDecisionIsLinkedToPro()) {
			qualityCheckResults.add(checkDecisionLinkedToPro(element));
		}
		return qualityCheckResults;
	}

	private QualityCriterionCheckResult checkDecisionLinkedToIssue(KnowledgeElement decision) {
		if (decision.getLinkedDecisionProblems().isEmpty()) {
			return new QualityCriterionCheckResult(QualityCriterionType.DECISION_LINKED_TO_ISSUE, true);
		}
		return new QualityCriterionCheckResult(QualityCriterionType.DECISION_LINKED_TO_ISSUE, false);
	}

	private QualityCriterionCheckResult checkDecisionStatus(KnowledgeElement decision) {
		if (decision.getStatus() == KnowledgeStatus.CHALLENGED) {
			return new QualityCriterionCheckResult(QualityCriterionType.DECISION_STATUS, true);
		} else {
			return new QualityCriterionCheckResult(QualityCriterionType.DECISION_STATUS, false);
		}
	}

	private QualityCriterionCheckResult checkDecisionLinkedToPro(KnowledgeElement decision) {
		if (decision.hasNeighborOfType(KnowledgeType.PRO)) {
			return new QualityCriterionCheckResult(QualityCriterionType.DECISION_LINKED_TO_PRO, false);
		}
		return new QualityCriterionCheckResult(QualityCriterionType.DECISION_LINKED_TO_PRO, true);
	}
}
