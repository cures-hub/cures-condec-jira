package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import java.util.Set;

import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import org.jgrapht.Graphs;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class IssueCompletenessCheck implements CompletenessCheck {

	private KnowledgeElement issue;
	private KnowledgeGraph graph;
	private Set<KnowledgeElement> neighbours;
	private String projectKey;
	boolean isComplete;


	@Override
	public boolean execute(KnowledgeElement issue) {
		this.issue = issue;
		graph = KnowledgeGraph.getOrCreate(this.issue.getProject());
		neighbours = Graphs.neighborSetOf(graph, this.issue);
		projectKey = this.issue.getProject().getProjectKey();
		boolean hasToBeLinkedToAlternative =
			ConfigPersistenceManager.getDefinitionOfDone(projectKey).isIssueIsLinkedToAlternative();
		isComplete = hasNeighbourOfType(KnowledgeType.ISSUE);
		if (hasToBeLinkedToAlternative && isComplete) {
			isComplete = hasNeighbourOfType(KnowledgeType.ALTERNATIVE);
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
