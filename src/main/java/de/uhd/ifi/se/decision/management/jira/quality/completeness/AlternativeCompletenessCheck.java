package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import java.util.Set;

import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import org.jgrapht.Graphs;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class AlternativeCompletenessCheck implements CompletenessCheck {

	private KnowledgeElement alternative;
	private KnowledgeGraph graph;
	private Set<KnowledgeElement> neighbours;
	private String projectKey;

	@Override
	public boolean execute(KnowledgeElement alternative) {
		this.alternative = alternative;
		graph = KnowledgeGraph.getOrCreate(alternative.getProject());
		neighbours = Graphs.neighborSetOf(graph, alternative);
		projectKey = alternative.getProject().getProjectKey();
		boolean isComplete;
		boolean hasToBeLinkedToArgument =
			ConfigPersistenceManager.getDefinitionOfDone(projectKey).isAlternativeIsLinkedToArgument();
		isComplete = hasNeighbourOfType(KnowledgeType.ISSUE);
		if (hasToBeLinkedToArgument && isComplete) {
			isComplete = hasNeighbourOfType(KnowledgeType.ARGUMENT);
		}
		return isComplete;
	}


	private boolean hasNeighbourOfType(KnowledgeType knowledgeType) {
		for (KnowledgeElement knowledgeElement : neighbours) {
			if (knowledgeElement.getType() == knowledgeType)
			return true;
		}
		return false;
	}

}
