package de.uhd.ifi.se.decision.management.jira.view.macros;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class IssueMacro extends AbstractKnowledgeClassificationMacro {

	@Override
	public String getColor() {
		return "#F2F5A9";
	}

	@Override
	public String getKnowledgeType() {
		return KnowledgeType.ISSUE.toString().toLowerCase();
	}
}