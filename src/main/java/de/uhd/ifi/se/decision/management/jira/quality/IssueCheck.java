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

		if (!isValidDecisionLinkedToDecisionProblem(element)) {
			qualityCheckResults
					.add(new QualityCriterionCheckResult(QualityCriterionType.ISSUE_LINKED_TO_DECISION, true));
		} else {
			qualityCheckResults
					.add(new QualityCriterionCheckResult(QualityCriterionType.ISSUE_LINKED_TO_DECISION, false));
		}

		if (!isResolved()) {
			qualityCheckResults
					.add(new QualityCriterionCheckResult(QualityCriterionType.DECISION_PROBLEM_STATUS, true));
		} else {
			qualityCheckResults
					.add(new QualityCriterionCheckResult(QualityCriterionType.DECISION_PROBLEM_STATUS, false));
		}

		if (definitionOfDone.isIssueIsLinkedToAlternative()) {
			if (!hasAlternative()) {
				qualityCheckResults
						.add(new QualityCriterionCheckResult(QualityCriterionType.ISSUE_LINKED_TO_ALTERNATIVE, true));
			} else {
				qualityCheckResults
						.add(new QualityCriterionCheckResult(QualityCriterionType.ISSUE_LINKED_TO_ALTERNATIVE, false));
			}
		}

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

	private boolean isResolved() {
		return element.getStatus() != KnowledgeStatus.UNRESOLVED;
	}

	private boolean hasAlternative() {
		return element.hasNeighborOfType(KnowledgeType.ALTERNATIVE);
	}

}
