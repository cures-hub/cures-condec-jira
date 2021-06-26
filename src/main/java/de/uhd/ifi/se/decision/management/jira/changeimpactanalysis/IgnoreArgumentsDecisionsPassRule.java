package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;

public class IgnoreArgumentsDecisionsPassRule implements PassPredicate {

	@Override
	public boolean pass(KnowledgeElement root, Double srcImpact, KnowledgeElement next, Double destImpact, Link link) {
		return (!root.getType().equals(KnowledgeType.ARGUMENT)
			&& !root.getType().equals(KnowledgeType.PRO)
			&& !root.getType().equals(KnowledgeType.CON))
			||
			(!next.getType().equals(KnowledgeType.ALTERNATIVE)
				&& !next.getType().equals(KnowledgeType.DECISION))
			|| srcImpact.equals(1.0);
	}
}
