package de.uhd.ifi.se.decision.management.jira.quality;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

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
		qualityCheckResults.add(checkDecisionLevelAssigned(element));
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

	public static QualityCriterionCheckResult checkDecisionLevelAssigned(KnowledgeElement element) {
		List<String> decisionGroups = element.getDecisionGroups();
		QualityCriterionCheckResult checkResult = new QualityCriterionCheckResult(
				QualityCriterionType.DECISION_LEVEL_AND_GROUPS, true);
		if (decisionGroups.size() == 1) {
			checkResult.setExplanation("A decision level is chosen, but at least one decision group is missing.");
		}
		if (decisionGroups.size() < 2) {
			return checkResult;
		}
		checkResult.setCriterionViolated(false);
		checkResult.setExplanation("The following decision level and decision group(s) are assigned to the "
				+ element.getTypeAsString() + ": " + StringUtils.join(decisionGroups, ", "));
		return checkResult;
	}
}