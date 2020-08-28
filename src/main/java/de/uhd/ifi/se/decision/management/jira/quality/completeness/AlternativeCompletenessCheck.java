package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import org.jgrapht.Graphs;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class AlternativeCompletenessCheck extends KnowledgeElementCompletenessCheck implements CompletenessCheck {

	@Override
	public boolean execute(KnowledgeElement alternative) {
		knowledgeElement = alternative;
		graph = KnowledgeGraph.getOrCreate(alternative.getProject());
		neighbours = Graphs.neighborSetOf(graph, alternative);
		projectKey = alternative.getProject().getProjectKey();
		return isCompleteAccordingToDefault() && isCompleteAccordingToSettings();
	}

	@Override
	protected boolean isCompleteAccordingToDefault() {
		return hasNeighbourOfType(KnowledgeType.ISSUE);
	}

	@Override
	protected boolean isCompleteAccordingToSettings() {
		boolean hasToBeLinkedToArgument =
			ConfigPersistenceManager.getDefinitionOfDone(projectKey).isAlternativeIsLinkedToArgument();
		if (hasToBeLinkedToArgument) {
			return hasNeighbourOfType(KnowledgeType.ARGUMENT);
		}
		else return true;
	}
}
