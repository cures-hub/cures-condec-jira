package de.uhd.ifi.se.decision.management.jira.quality.completeness;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class IssueCompletenessCheck implements CompletenessCheck {

	KnowledgeElement issue;
	String projectKey;

	@Override
	public boolean execute(KnowledgeElement issue) {
		this.issue = issue;
		projectKey = issue.getProject().getProjectKey();
		return isCompleteAccordingToDefault() && isCompleteAccordingToSettings();
	}

	@Override
	public boolean isCompleteAccordingToDefault() {
		return issue.hasNeighbourOfType(KnowledgeType.DECISION);
	}

	@Override
	public boolean isCompleteAccordingToSettings() {
		boolean hasToBeLinkedToArgument =
			ConfigPersistenceManager.getDefinitionOfDone(projectKey).isIssueIsLinkedToAlternative();
		if (hasToBeLinkedToArgument) {
			return issue.hasNeighbourOfType(KnowledgeType.ALTERNATIVE);
		}
		else return true;
	}
}
