package de.uhd.ifi.se.decision.management.jira.view.macros;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class AlternativeMacro extends AbstractKnowledgeClassificationMacro {

	@Override
	public String getColor() {
		return "#FFF6E8";
	}

	@Override
	public KnowledgeType getKnowledgeType() {
		return KnowledgeType.ALTERNATIVE;
	}
}