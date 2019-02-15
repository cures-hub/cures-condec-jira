package de.uhd.ifi.se.decision.management.jira.view.macros;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class ProMacro extends AbstractKnowledgeClassificationMacro {

	@Override
	public String getColor() {
		return "#b9f7c0";
	}

	@Override
	public KnowledgeType getKnowledgeType() {
		return KnowledgeType.PRO;
	}
}