package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import org.jgrapht.Graphs;

import java.util.List;

public class IssueCompletionCheck implements CompletionCheck {
	@Override
	public boolean execute(KnowledgeElement issue) {
		KnowledgeGraph graph = KnowledgeGraph.getOrCreate(issue.getProject());
		List<KnowledgeElement> neighbours = Graphs.neighborListOf(graph, issue);
		for (KnowledgeElement knowledgeElement : neighbours
		) {
			if (knowledgeElement.getType() == KnowledgeType.DECISION || knowledgeElement.getType() == KnowledgeType.ALTERNATIVE) {
				return true;
			}
		}
		return false;
	}
}
