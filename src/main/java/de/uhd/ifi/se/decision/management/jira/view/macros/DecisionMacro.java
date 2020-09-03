package de.uhd.ifi.se.decision.management.jira.view.macros;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

/**
 * Macro to mark (i.e. annotate/classifiy) text in the description or comments
 * of a Jira issue as a decision (= picked solution option/solution for an
 * issue). Each macro class needs to be added in the atlassian-plugin.xml file.
 */
public class DecisionMacro extends AbstractKnowledgeClassificationMacro {

	@Override
	public KnowledgeType getKnowledgeType() {
		return KnowledgeType.DECISION;
	}
}