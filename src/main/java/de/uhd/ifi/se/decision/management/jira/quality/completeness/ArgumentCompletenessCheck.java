package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import java.util.Set;

import org.jgrapht.Graphs;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class ArgumentCompletenessCheck implements CompletenessCheck {

	@Override
	public boolean execute(KnowledgeElement argument) {
		KnowledgeGraph graph = KnowledgeGraph.getOrCreate(argument.getProject());
		Set<KnowledgeElement> neighbours = Graphs.neighborSetOf(graph, argument);
		for (KnowledgeElement knowledgeElement : neighbours) {
			if (knowledgeElement.getType() == KnowledgeType.ALTERNATIVE
					|| knowledgeElement.getType() == KnowledgeType.DECISION) {
				return true;
			}
		}
		return false;
	}

}
