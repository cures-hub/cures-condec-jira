package de.uhd.ifi.se.decision.management.jira.rationale.backlog;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import org.jgrapht.Graphs;

import java.util.List;

public class AlternativeCompletionCheck implements CompletionCheck {
	@Override
	public boolean execute(KnowledgeElement alternative) {
		KnowledgeGraph graph = KnowledgeGraph.getOrCreate(alternative.getProject());
		List<KnowledgeElement> neighbours = Graphs.neighborListOf(graph, alternative);
		for (KnowledgeElement knowledgeElement : neighbours
		) {
			if (knowledgeElement.getType() == KnowledgeType.ISSUE) {
				return true;
			}
		}
		return false;
	}

}
