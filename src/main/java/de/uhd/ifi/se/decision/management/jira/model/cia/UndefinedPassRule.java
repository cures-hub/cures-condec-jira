package de.uhd.ifi.se.decision.management.jira.model.cia;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Link;

public class UndefinedPassRule implements PassPredicate {
	@Override
	public boolean pass(KnowledgeElement root, Double srcImpact, KnowledgeElement next, Double destImpact, Link link) {
		return true;
	}
}
