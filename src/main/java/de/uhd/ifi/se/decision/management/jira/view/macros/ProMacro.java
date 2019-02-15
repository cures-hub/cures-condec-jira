package de.uhd.ifi.se.decision.management.jira.view.macros;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class ProMacro extends AbstractKnowledgeClassificationMacro {

	@Override
	public String getColor() {
		return "#DEFADE";
	}

	@Override
	public KnowledgeType getKnowledgeType() {
		return KnowledgeType.PRO;
	}
}