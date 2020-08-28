package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import org.jgrapht.Graphs;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class IssueKnowledgeElementCompletenessCheck extends KnowledgeElementCompletenessCheck implements CompletionCheck {

	@Override
	public boolean execute(KnowledgeElement issue) {
		knowledgeElement = issue;
		graph = KnowledgeGraph.getOrCreate(issue.getProject());
		neighbours = Graphs.neighborSetOf(graph, issue);
		projectKey = issue.getProject().getProjectKey();
		return isCompleteAccordingToDefault() && isCompleteAccordingToSettings();
	}

	@Override
	protected boolean isCompleteAccordingToDefault() {
		return hasNeighbourOfType(KnowledgeType.DECISION);
	}

	@Override
	protected boolean isCompleteAccordingToSettings() {
		boolean hasToBeLinkedToArgument =
			ConfigPersistenceManager.getDefinitionOfDone(projectKey).isIssueIsLinkedToAlternative();
		if (hasToBeLinkedToArgument) {
			return hasNeighbourOfType(KnowledgeType.ALTERNATIVE);
		}
		else return true;
	}
}
