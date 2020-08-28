package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import org.jgrapht.Graphs;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class DecisionCompletenessCheck extends CompletenessCheck {


	@Override
	public boolean execute(KnowledgeElement decision) {
		knowledgeElement = decision;
		graph = KnowledgeGraph.getOrCreate(decision.getProject());
		neighbours = Graphs.neighborSetOf(graph, decision);
		projectKey = decision.getProject().getProjectKey();
		return isCompleteAccordingToDefault() && isCompleteAccordingToSettings();
	}

	@Override
	protected boolean isCompleteAccordingToDefault() {
		return hasNeighbourOfType(KnowledgeType.ISSUE);
	}

	@Override
	protected boolean isCompleteAccordingToSettings() {
		boolean hasToBeLinkedToArgument =
			ConfigPersistenceManager.getDefinitionOfDone(projectKey).isDecisionIsLinkedToPro();
		if (hasToBeLinkedToArgument) {
			return hasNeighbourOfType(KnowledgeType.PRO);
		}
		else return true;
	}

}
