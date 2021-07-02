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
public class IssueCheck implements KnowledgeElementCheck<KnowledgeElement> {

	private KnowledgeElement issue;
	private String projectKey;

	@Override
	public boolean execute(KnowledgeElement decisionProblem) {
		this.issue = decisionProblem;
		projectKey = decisionProblem.getProject().getProjectKey();
		return isCompleteAccordingToDefault() && isCompleteAccordingToSettings();
	}

	@Override
	public boolean isCompleteAccordingToDefault() {
		return isValidDecisionLinkedToDecisionProblem(issue)
				&& issue.getStatus() != KnowledgeStatus.UNRESOLVED;
	}

	@Override
	public boolean isCompleteAccordingToSettings() {
		boolean hasToBeLinkedToAlternative = ConfigPersistenceManager.getDefinitionOfDone(projectKey)
				.isIssueIsLinkedToAlternative();
		if (hasToBeLinkedToAlternative) {
			return issue.hasNeighborOfType(KnowledgeType.ALTERNATIVE);
		}
		return true;
	}

	@Override
	public List<QualityProblem> getFailedCriteria(KnowledgeElement decisionProblem) {
		List<QualityProblem> failedCriteria = new ArrayList<>();

		if (!isValidDecisionLinkedToDecisionProblem(decisionProblem)) {
			failedCriteria.add(QualityProblem.ISSUEDOESNTHAVEDECISION);
		}

		if (decisionProblem.getStatus() == KnowledgeStatus.UNRESOLVED) {
			failedCriteria.add(QualityProblem.ISSUEISUNRESOLVED);
		}

		boolean hasToBeLinkedToAlternative = ConfigPersistenceManager.getDefinitionOfDone(projectKey)
			.isIssueIsLinkedToAlternative();
		if (hasToBeLinkedToAlternative && !decisionProblem.hasNeighborOfType(KnowledgeType.ALTERNATIVE)) {
			failedCriteria.add(QualityProblem.ISSUEDOESNTHAVEALTERNATIVE);
		}

		return failedCriteria;
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

}
