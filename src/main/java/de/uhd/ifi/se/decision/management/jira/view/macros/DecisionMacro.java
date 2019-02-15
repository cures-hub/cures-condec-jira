package de.uhd.ifi.se.decision.management.jira.view.macros;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class DecisionMacro extends AbstractKnowledgeClassificationMacro {

	@Override
	public String getColor() {
		return "#FCE3BE";
	}

	@Override
	public KnowledgeType getKnowledgeType() {
		return KnowledgeType.DECISION;
	}
}