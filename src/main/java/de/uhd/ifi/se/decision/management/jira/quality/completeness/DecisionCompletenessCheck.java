package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import java.util.Set;

import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import org.jgrapht.Graphs;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class DecisionCompletenessCheck implements CompletenessCheck {

	private KnowledgeElement decision;
	private KnowledgeGraph graph;
	private Set<KnowledgeElement> neighbours;
	private String projectKey;
	boolean isComplete;


	@Override
	public boolean execute(KnowledgeElement decision) {
		this.decision = decision;
		graph = KnowledgeGraph.getOrCreate(this.decision.getProject());
		neighbours = Graphs.neighborSetOf(graph, this.decision);
		projectKey = this.decision.getProject().getProjectKey();
		boolean hasToBeLinkedToPro =
			ConfigPersistenceManager.getDefinitionOfDone(projectKey).isDecisionIsLinkedToPro();
		isComplete = hasNeighbourOfType(KnowledgeType.ISSUE);
		if (hasToBeLinkedToPro && isComplete) {
			isComplete = hasNeighbourOfType(KnowledgeType.PRO);
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
