package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import java.util.Set;

import org.jgrapht.Graphs;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class AlternativeCompletenessCheck implements CompletenessCheck {

	@Override
	public boolean execute(KnowledgeElement alternative) {
		KnowledgeGraph graph = KnowledgeGraph.getOrCreate(alternative.getProject());
		Set<KnowledgeElement> neighbours = Graphs.neighborSetOf(graph, alternative);
		for (KnowledgeElement knowledgeElement : neighbours) {
			if (knowledgeElement.getType() == KnowledgeType.ISSUE) {
				return true;
			}
		}
		return false;
	}

}
