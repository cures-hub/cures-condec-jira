package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import org.jgrapht.Graphs;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class ArgumentKnowledgeElementCompletenessCheck extends KnowledgeElementCompletenessCheck implements CompletionCheck {

	@Override
	public boolean execute(KnowledgeElement argument) {
		knowledgeElement = argument;
		graph = KnowledgeGraph.getOrCreate(argument.getProject());
		neighbours = Graphs.neighborSetOf(graph, argument);
		projectKey = argument.getProject().getProjectKey();
		return isCompleteAccordingToDefault() && isCompleteAccordingToSettings();
	}

	@Override
	protected boolean isCompleteAccordingToDefault() {
		return hasNeighbourOfType(KnowledgeType.DECISION) || hasNeighbourOfType(KnowledgeType.ALTERNATIVE);
	}

	@Override
	protected boolean isCompleteAccordingToSettings() {
		return true;
	}

}
