package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Link;

public interface PassPredicate {

	boolean pass(KnowledgeElement root, Double srcImpact, KnowledgeElement next, Double destImpact, Link link);
}
