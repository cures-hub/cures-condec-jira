package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;

public class IgnoreDecisionIncoming implements PassPredicate {

	@Override
	public boolean pass(KnowledgeElement root, Double srcImpact, KnowledgeElement next, Double destImpact, Link link) {
		return !(root.getType().equals(KnowledgeType.DECISION) && root.equals(link.getTarget()));
	}
}
