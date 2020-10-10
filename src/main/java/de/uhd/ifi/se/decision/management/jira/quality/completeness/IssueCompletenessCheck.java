package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
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
		return isDecisionProblemResolved(issue);
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

	public static boolean isDecisionProblemResolved(KnowledgeElement issue) {
		return issue.hasNeighborOfType(KnowledgeType.DECISION);
	}
}
