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
public class DecisionProblemCompletenessCheck implements CompletenessCheck<KnowledgeElement> {

	private final String ISSUEDOESNTHAVEDECISION = "Issue doesn't have a valid decision!";
	private final String ISSUEISUNRESOLVED = "Issue is unresolved!";
	private final String ISSUEDOESNTHAVEALTERNATIVE = "Issue doesn't have an alternative!";

	private KnowledgeElement decisionProblem;
	private String projectKey;

	@Override
	public boolean execute(KnowledgeElement decisionProblem) {
		this.decisionProblem = decisionProblem;
		projectKey = decisionProblem.getProject().getProjectKey();
		return isCompleteAccordingToDefault() && isCompleteAccordingToSettings();
	}

	@Override
	public boolean isCompleteAccordingToDefault() {
		return isValidDecisionLinkedToDecisionProblem(decisionProblem)
				&& decisionProblem.getStatus() != KnowledgeStatus.UNRESOLVED;
	}

	@Override
	public boolean isCompleteAccordingToSettings() {
		boolean hasToBeLinkedToAlternative = ConfigPersistenceManager.getDefinitionOfDone(projectKey)
				.isIssueIsLinkedToAlternative();
		if (hasToBeLinkedToAlternative) {
			return decisionProblem.hasNeighborOfType(KnowledgeType.ALTERNATIVE);
		}
		return true;
	}

	@Override
	public List<String> getFailedCriteria(KnowledgeElement decisionProblem) {
		List<String> failedCriteria = new ArrayList<>();

		if (!isValidDecisionLinkedToDecisionProblem(decisionProblem)) {
			failedCriteria.add(ISSUEDOESNTHAVEDECISION);
		}

		if (decisionProblem.getStatus() == KnowledgeStatus.UNRESOLVED) {
			failedCriteria.add(ISSUEISUNRESOLVED);
		}

		boolean hasToBeLinkedToAlternative = ConfigPersistenceManager.getDefinitionOfDone(projectKey)
			.isIssueIsLinkedToAlternative();
		if (hasToBeLinkedToAlternative) {
			if (!decisionProblem.hasNeighborOfType(KnowledgeType.ALTERNATIVE)) {
				failedCriteria.add(ISSUEDOESNTHAVEALTERNATIVE);
			}
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
