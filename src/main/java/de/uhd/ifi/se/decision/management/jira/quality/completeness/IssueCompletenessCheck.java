package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import java.util.Set;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;

public class IssueCompletenessCheck implements CompletenessCheck {

	private KnowledgeElement issue;
	private String projectKey;

	@Override
	public boolean execute(KnowledgeElement issue) {
		this.issue = issue;
		projectKey = issue.getProject().getProjectKey();
		return isCompleteAccordingToDefault() && isCompleteAccordingToSettings();
	}

	@Override
	public boolean isCompleteAccordingToDefault() {
		return isValidDecisionLinkedToDecisionProblem(issue) && issue.getStatus() != KnowledgeStatus.UNRESOLVED;
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

	/**
	 * @param issue
	 *            decision problem as a {@link KnowledgeElement} object.
	 * @return true if a valid decision is linked to the issue.
	 */
	public static boolean isValidDecisionLinkedToDecisionProblem(KnowledgeElement issue) {
		Set<KnowledgeElement> linkedDecisions = issue.getNeighborsOfType(KnowledgeType.DECISION);
		return !linkedDecisions.isEmpty()
				&& linkedDecisions.stream().anyMatch(decision -> decision.getStatus() != KnowledgeStatus.CHALLENGED
						&& decision.getStatus() != KnowledgeStatus.REJECTED);
	}
}
