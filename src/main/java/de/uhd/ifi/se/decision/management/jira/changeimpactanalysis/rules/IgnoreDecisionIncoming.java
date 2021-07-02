package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;

public class IgnoreDecisionIncoming implements ChangePropagationPredicate {

	@Override
	public boolean isChangePropagated(KnowledgeElement root, double srcImpact, KnowledgeElement next, double destImpact, Link link) {
		return !(root.getType() == KnowledgeType.DECISION && root.equals(link.getTarget()));
	}
}
