package de.uhd.ifi.se.decision.management.jira.view.macros;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class DecisionMacro extends AbstractKnowledgeClassificationMacro {

	@Override
	public String getColor() {
		return "#c5f2f9";
	}

	@Override
	public KnowledgeType getKnowledgeType() {
		return KnowledgeType.DECISION;
	}
}