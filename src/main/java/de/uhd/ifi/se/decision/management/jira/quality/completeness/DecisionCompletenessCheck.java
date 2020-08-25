package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import java.util.List;

import org.jgrapht.Graphs;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class DecisionCompletenessCheck implements CompletenessCheck {

	@Override
	public boolean execute(KnowledgeElement decision) {
		KnowledgeGraph graph = KnowledgeGraph.getOrCreate(decision.getProject());
		List<KnowledgeElement> neighbours = Graphs.neighborListOf(graph, decision);
		for (KnowledgeElement knowledgeElement : neighbours) {
			if (knowledgeElement.getType() == KnowledgeType.ISSUE) {
				return true;
			}
		}
		return false;
	}

}
