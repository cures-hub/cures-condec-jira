package de.uhd.ifi.se.decision.management.jira.view.macros;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class AlternativeMacro extends AbstractKnowledgeClassificationMacro {

	@Override
	public String getColor() {
		return "#f1ccf9";
	}

	@Override
	public String getKnowledgeType() {
		return KnowledgeType.ALTERNATIVE.toString().toLowerCase();
	}
}