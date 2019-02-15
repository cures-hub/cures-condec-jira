package de.uhd.ifi.se.decision.management.jira.view.macros;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class ConMacro extends AbstractKnowledgeClassificationMacro {

	@Override
	public String getColor() {
		return "#ffdeb5";
	}

	@Override
	public String getKnowledgeType() {
		return KnowledgeType.CON.toString().toLowerCase();
	}
}