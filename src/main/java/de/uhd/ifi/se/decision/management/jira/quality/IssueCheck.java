package de.uhd.ifi.se.decision.management.jira.quality;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

/**
 * Checks whether a decision problem (=issue, question, goal, ...) fulfills the
 * {@link DefinitionOfDone}.
 */
public class IssueCheck extends KnowledgeElementCheck {

	public IssueCheck(KnowledgeElement elementToBeChecked) {
		super(elementToBeChecked);
	}

	@Override
	public List<QualityCriterionCheckResult> getQualityCheckResult(DefinitionOfDone definitionOfDone) {
		List<QualityCriterionCheckResult> qualityCheckResults = new ArrayList<>();
		qualityCheckResults.add(checkIssueLinkedToValidDecision(element));
		qualityCheckResults.add(checkIssueStatus(element));
		if (definitionOfDone.isIssueIsLinkedToAlternative()) {
			qualityCheckResults.add(checkIssueLinkedToAlternative(element));
		}
		qualityCheckResults.add(DecisionCheck.checkDecisionLevelAssigned(element));
		return qualityCheckResults;
	}

	/**
	 * @param decisionProblem
	 *            decision problem (issue) as a {@link KnowledgeElement} object.
	 * @return true if a valid decision is linked to the decision problem.
	 */
	public static boolean isValidDecisionLinkedToDecisionProblem(KnowledgeElement decisionProblem) {
		Set<KnowledgeElement> linkedDecisions = decisionProblem.getNeighborsOfType(KnowledgeType.DECISION);
		linkedDecisions.addAll(decisionProblem.getNeighborsOfType(KnowledgeType.SOLUTION));
		return !linkedDecisions.isEmpty()
				&& linkedDecisions.stream().anyMatch(decision -> decision.getStatus() != KnowledgeStatus.CHALLENGED
						&& decision.getStatus() != KnowledgeStatus.REJECTED);
	}

	private QualityCriterionCheckResult checkIssueLinkedToValidDecision(KnowledgeElement issue) {
		if (isValidDecisionLinkedToDecisionProblem(issue)) {
			return new QualityCriterionCheckResult(QualityCriterionType.ISSUE_LINKED_TO_DECISION, false);
		}
		return new QualityCriterionCheckResult(QualityCriterionType.ISSUE_LINKED_TO_DECISION, true);
	}

	private QualityCriterionCheckResult checkIssueStatus(KnowledgeElement issue) {
		if (issue.getStatus() != KnowledgeStatus.UNRESOLVED) {
			return new QualityCriterionCheckResult(QualityCriterionType.ISSUE_STATUS, false);
		}
		return new QualityCriterionCheckResult(QualityCriterionType.ISSUE_STATUS, true);
	}

	private QualityCriterionCheckResult checkIssueLinkedToAlternative(KnowledgeElement issue) {
		if (issue.hasNeighborOfType(KnowledgeType.ALTERNATIVE)) {
			return new QualityCriterionCheckResult(QualityCriterionType.ISSUE_LINKED_TO_ALTERNATIVE, false);
		}
		return new QualityCriterionCheckResult(QualityCriterionType.ISSUE_LINKED_TO_ALTERNATIVE, true);
	}

}
