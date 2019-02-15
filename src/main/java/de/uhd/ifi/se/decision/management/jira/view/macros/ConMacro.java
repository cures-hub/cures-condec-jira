package de.uhd.ifi.se.decision.management.jira.view.macros;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class ConMacro extends AbstractKnowledgeClassificationMacro {

	@Override
	public String getColor() {
		return "#FFE7E7";
	}

	@Override
	public KnowledgeType getKnowledgeType() {
		return KnowledgeType.CON;
	}
}