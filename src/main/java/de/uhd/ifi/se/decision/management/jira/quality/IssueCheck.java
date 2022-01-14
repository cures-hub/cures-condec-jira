package de.uhd.ifi.se.decision.management.jira.quality;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;

/**
 * Checks whether a decision problem (=issue, question, goal, ...) fulfills the
 * {@link DefinitionOfDone}.
 */
public class IssueCheck implements KnowledgeElementCheck {

	private KnowledgeElement issue;

	@Override
	public boolean execute(KnowledgeElement issue) {
		this.issue = issue;
		String projectKey = issue.getProject().getProjectKey();
		DefinitionOfDone definitionOfDone = ConfigPersistenceManager.getDefinitionOfDone(projectKey);
		return isCompleteAccordingToDefault() && isCompleteAccordingToSettings(definitionOfDone);
	}

	@Override
	public boolean isCompleteAccordingToDefault() {
		return isValidDecisionLinkedToDecisionProblem(issue) && isResolved();
	}

	@Override
	public boolean isCompleteAccordingToSettings(DefinitionOfDone definitionOfDone) {
		return !definitionOfDone.isIssueIsLinkedToAlternative() || hasAlternative();
	}

	@Override
	public List<QualityCriterionCheckResult> getQualityCheckResult(KnowledgeElement issue,
			DefinitionOfDone definitionOfDone) {
		this.issue = issue;

		List<QualityCriterionCheckResult> qualityCheckResults = new ArrayList<>();

		if (!isValidDecisionLinkedToDecisionProblem(issue)) {
			qualityCheckResults.add(new QualityCriterionCheckResult(QualityCriterionType.ISSUE_LINKED_TO_DECISION, true));
		} else {
			qualityCheckResults
					.add(new QualityCriterionCheckResult(QualityCriterionType.ISSUE_LINKED_TO_DECISION, false));
		}

		if (!isResolved()) {
			qualityCheckResults.add(new QualityCriterionCheckResult(QualityCriterionType.ISSUE_RESOLUTION, true));
		} else {
			qualityCheckResults.add(new QualityCriterionCheckResult(QualityCriterionType.ISSUE_RESOLUTION, false));
		}

		if (definitionOfDone.isIssueIsLinkedToAlternative()) {
			if (!hasAlternative()) {
				qualityCheckResults
						.add(new QualityCriterionCheckResult(QualityCriterionType.ISSUE_LINKED_TO_ALTERNATIVE, true));
			} else {
				qualityCheckResults.add(
						new QualityCriterionCheckResult(QualityCriterionType.ISSUE_LINKED_TO_ALTERNATIVE, false));
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
		return issue.getStatus() != KnowledgeStatus.UNRESOLVED;
	}

	private boolean hasAlternative() {
		return issue.hasNeighborOfType(KnowledgeType.ALTERNATIVE);
	}

}
