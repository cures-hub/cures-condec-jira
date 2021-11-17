package de.uhd.ifi.se.decision.management.jira.quality.completeness;

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
		return hasAlternative(definitionOfDone);
	}

	@Override
	public List<QualityProblem> getQualityProblems(KnowledgeElement issue, DefinitionOfDone definitionOfDone) {
		this.issue = issue;

		List<QualityProblem> qualityProblems = new ArrayList<>();

		if (!isValidDecisionLinkedToDecisionProblem(issue)) {
			qualityProblems.add(new QualityProblem(QualityProblemType.ISSUE_DOESNT_HAVE_DECISION));
		}

		if (!isResolved()) {
			qualityProblems.add(new QualityProblem(QualityProblemType.ISSUE_IS_UNRESOLVED));
		}

		if (!hasAlternative(definitionOfDone)) {
			qualityProblems.add(new QualityProblem(QualityProblemType.ISSUE_DOESNT_HAVE_ALTERNATIVE));
		}

		return qualityProblems;
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

	private boolean hasAlternative(DefinitionOfDone definitionOfDone) {
		boolean hasToBeLinkedToAlternative = definitionOfDone.isIssueIsLinkedToAlternative();
		if (hasToBeLinkedToAlternative) {
			return issue.hasNeighborOfType(KnowledgeType.ALTERNATIVE);
		}
		return true;
	}

}
